# Meetup RSVP — Demo del Event Publication Registry de Spring Modulith

*Read in English: [README.md](./README.md)*

Una app mínima de Spring Modulith construida alrededor de una sola idea: el **event publication registry**. Cada evento publicado a través del `ApplicationEventPublisher` de Spring escribe una fila en una tabla de base de datos (`event_publication`) *antes* de que se ejecute ningún listener. Los listeners se marcan como completados cuando terminan sin errores. Si un listener estaba caído, lanzó una excepción, o la app se cayó a mitad de camino, la fila se queda **pendiente** (`completion_date IS NULL`) — y Spring Modulith la reintenta automáticamente la próxima vez que arranca la app.

Este repo es el complemento práctico de ese mecanismo: dos módulos, un evento, un flag para forzar un fallo, y el SQL para ver al registry hacer su trabajo.

## Dominio

- `events` — crea meetups y RSVPs. Publica `RsvpReceived`.
- `notifications` — escucha `RsvpReceived` y escribe una fila `Confirmation`.

Crear un meetup o hacer un RSVP es un write normal. La parte interesante es lo que pasa *después* de guardar el RSVP: el service publica un evento, y todo lo que depende de eso (la notificación) pasa por el registry en vez de por una llamada directa a un método.

## Requisitos previos

- Java 25
- Docker (Postgres levanta solo con el `compose.yaml` incluido, sin configuración manual)

## Arranque rápido

```bash
./mvnw spring-boot:run
```

El soporte de docker-compose de Spring Boot levanta Postgres automáticamente (puerto `5455` en el host, ver `compose.yaml`). La app escucha en `:8080`.

```bash
# crear un meetup
curl -X POST http://localhost:8080/meetups \
  -H 'Content-Type: application/json' \
  -d '{"title":"Estudio de Java","date":"2026-05-15T18:00:00"}'

# hacer RSVP (usa el id devuelto arriba)
curl -X POST http://localhost:8080/meetups/1/rsvp \
  -H 'Content-Type: application/json' \
  -d '{"name":"Dan","email":"dan@example.com"}'
```

`date` es un `LocalDateTime`, así que necesita el componente de hora (`T18:00:00`). Ver [`client.http`](./client.http) para el mismo request en formato IntelliJ HTTP Client.

## La tabla `event_publication`

Esta es la tabla que usa Spring Modulith como outbox / registry de eventos. Se crea automáticamente porque `spring.modulith.events.jdbc.schema-initialization.enabled=true` está seteado en `application.yml` — nada en `schema.sql` la crea.

```sql
create table public.event_publication
(
    id                     uuid                     not null
        primary key,
    listener_id            text                     not null,
    event_type             text                     not null,
    serialized_event       text                     not null,
    publication_date       timestamp with time zone not null,
    completion_date        timestamp with time zone,
    status                 text,
    completion_attempts    integer,
    last_resubmission_date timestamp with time zone
);

create index event_publication_serialized_event_hash_idx
    on public.event_publication using hash (serialized_event);

create index event_publication_by_completion_date_idx
    on public.event_publication (completion_date);
```

### Columna por columna

| Columna | Para qué sirve |
|---|---|
| `id` | Primary key del registro de publicación en sí (no del evento). |
| `listener_id` | Identifica **a qué listener** corresponde esa fila. Spring Modulith escribe **una fila por listener**, no una fila por evento — si dos componentes escucharan `RsvpReceived`, tendrías dos filas por cada RSVP, cada una rastreada de forma independiente. |
| `event_type` | Nombre completo (fully-qualified) de la clase del evento, por ejemplo `com.tutorial.two.modulith.events.RsvpReceived`. |
| `serialized_event` | El payload del evento, serializado a JSON (Jackson por defecto) para poder reproducirlo más tarde sin el objeto original en memoria. |
| `publication_date` | Cuándo se publicó el evento, justo antes de invocar al listener. |
| `completion_date` | **El campo del que depende todo el mecanismo.** `null` = todavía pendiente / no procesado. Con valor = el listener terminó sin lanzar excepción. |
| `status` | Estado de la publicación (`PUBLISHED`, `COMPLETED`, etc. según la versión/configuración de Modulith). |
| `completion_attempts` | Cuántas veces se intentó un reintento/republish para esa fila. |
| `last_resubmission_date` | Timestamp del último intento de replay, si hubo alguno. |

### Cómo funciona realmente "verificar si el evento existe"

No hay un loop de polling que escanee buscando eventos nuevos. El flujo es transaccional y síncrono con el write:

1. `MeetupService.rsvp(...)` guarda la fila `Rsvp` y llama a `publisher.publishEvent(new RsvpReceived(...))` — un `ApplicationEvent` normal de Spring, nada específico de Modulith todavía.
2. El `EventPublicationRegistry` de Spring Modulith intercepta esto vía un `ApplicationListener` con prioridad alta. Por cada método anotado `@ApplicationModuleListener` que esté suscrito a ese tipo de evento, **inserta una fila** en `event_publication` con `completion_date = null`, dentro de la *misma* transacción que el guardado del RSVP.
3. `@ApplicationModuleListener` en `NotificationListener.on(RsvpReceived)` se expande a `@Async` + `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Transactional(propagation = REQUIRES_NEW)`. Entonces el listener solo se dispara **después** de que la transacción del RSVP haga commit — la fila del registry y la fila del RSVP quedan garantizadas de existir juntas, o de no existir ninguna.
4. Si el listener retorna normalmente, Modulith actualiza `completion_date` (y `status`) de esa fila para marcarla como completada.
5. Si el listener lanza una excepción (ver `notifications.fail: true` más abajo), nada actualiza la fila — se queda ahí con `completion_date IS NULL`. El listener tampoco reintenta de inmediato; queda "pendiente" (outstanding) esperando la próxima oportunidad de ser reproducido.
6. Al arrancar, como `republish-outstanding-events-on-restart: true` está activo, Modulith consulta `event_publication` buscando filas donde `completion_date IS NULL`, deserializa `serialized_event` de vuelta a una instancia de `RsvpReceived`, y vuelve a invocar al/los listener(s) correspondiente(s) por `listener_id`. Si esta vez tiene éxito, `completion_date` se completa.

Por eso existe el índice `hash` sobre `serialized_event` (búsqueda/dedup rápida por payload) y el índice b-tree normal sobre `completion_date` — esa es exactamente la columna que se filtra para encontrar trabajo pendiente, tanto durante el replay como en cualquier tarea de limpieza de completados.

En resumen: "verificar si un evento existe" no es un scan que vos escribís — es un `SELECT ... FROM event_publication WHERE completion_date IS NULL`, ejecutado por el propio Modulith al arrancar (y opcionalmente en un schedule, según la configuración).

## Estructura del proyecto

Dos módulos bajo `com.tutorial.two.modulith`, cada uno con su propio paquete `internal/`. La comunicación entre módulos pasa **únicamente** por el evento publicado — ningún módulo accede directamente a los internals de otro.

```
com.tutorial.two.modulith
├── SpringModulithTwoApplication.java
├── events/                          ← módulo 1: publica RsvpReceived
│   ├── Meetup.java                  ← público (lo devuelve el controller)
│   ├── Rsvp.java                    ← público (lo devuelve el controller)
│   ├── RsvpReceived.java            ← evento público
│   └── internal/
│       ├── MeetupController.java
│       ├── MeetupService.java
│       ├── MeetupRepository.java
│       └── RsvpRepository.java
└── notifications/                   ← módulo 2: cero tipos públicos
    └── internal/
        ├── Confirmation.java
        ├── ConfirmationRepository.java
        └── NotificationListener.java   (package-private, @ApplicationModuleListener)
```

El único contrato del módulo `notifications` con el resto del sistema es "estoy suscrito a `RsvpReceived`".

## El flujo de la demo

### Camino feliz

1. `POST /meetups` → fila en `meetup`.
2. `POST /meetups/{id}/rsvp` → fila en `rsvp`, y luego `NotificationListener` escribe una fila en `confirmation`.
3. La fila del registry para ese evento queda completada:

```sql
SELECT * FROM confirmation;
SELECT id, event_type, listener_id, completion_date FROM event_publication ORDER BY publication_date DESC;
```

### El fallo

1. Parar la app, poner `notifications.fail: true` en `application.yml`, reiniciar.
2. Hacer otro `POST` de RSVP. El listener lanza una excepción antes de guardar; no se escribe fila en `confirmation`.
3. El evento ahora es una **publicación pendiente**:

```sql
SELECT id, event_type, publication_date
FROM event_publication
WHERE completion_date IS NULL;
```

### El replay

1. Parar la app, poner `notifications.fail: false`, reiniciar.
2. `spring.modulith.events.republish-outstanding-events-on-restart=true` (ya está en `application.yml`) dispara un replay de cada fila pendiente al arrancar.
3. Aparece la fila en `confirmation`, y `completion_date` de esa fila del registry se completa.

Esa es la conclusión: entrega *at-least-once* para eventos in-process, gratis, sin tener que construir a mano una tabla outbox ni un loop de reintentos.

## Configuración clave (`application.yml`)

```yaml
spring:
  application:
    name: meetup
  sql:
    init:
      mode: always
  modulith:
    events:
      jdbc:
        schema-initialization:
          enabled: true                        # crea event_publication automáticamente
      republish-outstanding-events-on-restart: true   # la magia del replay

notifications:
  fail: false                                  # ponelo en true para probar el fallo
```

`meetup`, `rsvp` y `confirmation` salen de `src/main/resources/schema.sql` (Spring Data JDBC no tiene auto-DDL, así que `spring.sql.init.mode=always` corre ese script en cada arranque). `event_publication` **no** está en `schema.sql` — la crea Modulith directamente.

Si preferís gestionar `event_publication` vos mismo (producción, migraciones con Flyway/Liquibase, etc.), el DDL canónico por base de datos está en la documentación de Spring Modulith: [Schemas Appendix](https://docs.spring.io/spring-modulith/reference/appendix.html#schemas). Sacá la propiedad `schema-initialization` y corré ese SQL a través de tu herramienta de migraciones.

## Tests

```bash
./mvnw test
```

`SpringModulithTwoApplicationTests` hace dos cosas:

- `contextLoads()` — levanta el contexto completo de Spring (un Postgres real, levantado por el soporte de docker-compose de Boot).
- `verifiesModuleStructure()` — corre `ApplicationModules.of(SpringModulithTwoApplication.class)`, la verificación estructural de Modulith. Detecta en build time cualquier futuro acceso cruzado de un módulo hacia el paquete `internal/` de otro.

## Stack

- Java 25
- Spring Boot 4.0.6
- Spring Modulith 2.0.6
- Postgres (vía el soporte de Docker Compose de Spring Boot, `compose.yaml`, puerto `5455` en el host)
