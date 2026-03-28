# Hexagonal Architecture Demo

Proyecto de demostración de **Arquitectura Hexagonal** (Ports & Adapters) con Spring Boot y Java 21.

---

## Instalación y compilación

Desde la raíz del proyecto (donde está `pom.xml`):

```bash
mvn clean install -DskipTests
```

## Ejecución

```bash
./mvnw spring-boot:run
```

---

## Que es la Arquitectura Hexagonal

La Arquitectura Hexagonal, también llamada **Ports & Adapters**, fue propuesta por Alistair Cockburn. Su objetivo principal es **aislar el núcleo de negocio** de los detalles tecnológicos externos (bases de datos, frameworks, mensajería, APIs REST, etc.).

La idea central es que el negocio no debe saber si está siendo llamado desde una API REST, un consumer de Kafka o un test unitario. Tampoco debe saber si los datos se guardan en PostgreSQL, MongoDB o en memoria.

---

## Grafico de la Arquitectura

```
                    ┌─────────────────────────────────────────┐
                    │           MUNDO EXTERIOR                 │
                    │   HTTP Client / Postman / curl           │
                    └──────────────────┬──────────────────────┘
                                       │
                    ┌──────────────────▼──────────────────────┐
                    │         INFRASTRUCTURE / IN              │
                    │       (Adaptadores de Entrada)           │
                    │                                          │
                    │  ┌─────────────────────────────────┐    │
                    │  │  PostController  @RestController │    │
                    │  │  POST /api/posts/create          │    │
                    │  │  GET  /api/posts                 │    │
                    │  └────────────┬────────────────┬────┘    │
                    │               │                │         │
                    │  ┌────────────▼────┐  ┌────────▼──────┐ │
                    │  │CreatePostRequest│  │  PostWebMapper│ │
                    │  │   (DTO entrada) │  │  PostResponse │ │
                    │  └─────────────────┘  └───────────────┘ │
                    └──────────────────┬──────────────────────┘
                                       │ usa puertos (interfaces)
                    ┌──────────────────▼──────────────────────┐
                    │            APPLICATION / IN              │
                    │          (Puertos de Entrada)            │
                    │                                          │
                    │  ┌──────────────────────────────────┐   │
                    │  │  <<interface>>                   │   │
                    │  │  CreatePostUseCase               │   │
                    │  │  + create(title, content): void  │   │
                    │  └─────────────────┬────────────────┘   │
                    │                    │                     │
                    │  ┌─────────────────▼────────────────┐   │
                    │  │  <<interface>>                   │   │
                    │  │  ListPostUseCase                 │   │
                    │  │  + list(): List<Post>            │   │
                    │  └─────────────────┬────────────────┘   │
                    │                    │ implementado por    │
                    │         APPLICATION / SERVICE            │
                    │                    │                     │
                    │  ┌─────────────────▼────────────────┐   │
                    │  │  CreatePostService  @Service     │   │
                    │  │  ListPostService    @Service     │   │
                    │  │  - postRepository (interface)    │   │
                    │  └─────────────────┬────────────────┘   │
                    │                    │ usa puerto salida   │
                    │         APPLICATION / OUT                │
                    │                    │                     │
                    │  ┌─────────────────▼────────────────┐   │
                    │  │  <<interface>>                   │   │
                    │  │  PostRepository                  │   │
                    │  │  + save(Post): void              │   │
                    │  │  + update(id, Post): void        │   │
                    │  │  + findAll(): List<Post>         │   │
                    │  │  + findById(id): Post            │   │
                    │  └─────────────────┬────────────────┘   │
                    └──────────────────┬─┘                    │
                                       │ implementado por      │
                    ┌──────────────────▼──────────────────────┐
                    │         INFRASTRUCTURE / OUT             │
                    │       (Adaptadores de Salida)            │
                    │                                          │
                    │  ┌───────────────────────────────────┐  │
                    │  │  PostJpaAdapter  @Component       │  │
                    │  │  implements PostRepository        │  │
                    │  │  - postJpaRepository              │  │
                    │  │  - postJpaMapper                  │  │
                    │  └────────────┬──────────────────────┘  │
                    │               │ usa                      │
                    │  ┌────────────▼──────────────────────┐  │
                    │  │  PostJpaMapper   @Component       │  │
                    │  │  Post ↔ PostJpaEntity             │  │
                    │  └────────────┬──────────────────────┘  │
                    │               │ usa                      │
                    │  ┌────────────▼──────────────────────┐  │
                    │  │  PostJpaRepository                │  │
                    │  │  extends JpaRepository            │  │
                    │  │  <PostJpaEntity, String>          │  │
                    │  └────────────┬──────────────────────┘  │
                    │               │ mapea a                  │
                    │  ┌────────────▼──────────────────────┐  │
                    │  │  PostJpaEntity  @Entity           │  │
                    │  │  @Table("posts")                  │  │
                    │  │  id, title, content               │  │
                    │  └───────────────────────────────────┘  │
                    └──────────────────┬──────────────────────┘
                                       │
                    ┌──────────────────▼──────────────────────┐
                    │              PostgreSQL                  │
                    │  tabla: posts                           │
                    │  Flyway: V1__create_posts_table.sql     │
                    └─────────────────────────────────────────┘

                    ┌─────────────────────────────────────────┐
                    │               DOMAIN                     │
                    │   (Nucleo — sin dependencias externas)   │
                    │                                          │
                    │   Post.java  @Data                       │
                    │   - id: String                           │
                    │   - title: String                        │
                    │   - content: String                      │
                    └─────────────────────────────────────────┘

  Regla de dependencias:
  Infrastructure ──► Application ──► Domain   (nunca al revés)
```

---

## Las 3 Capas

### 1. Domain

El corazon del sistema. Contiene las **entidades de negocio** y la lógica de dominio pura.

- No depende de ninguna librería externa
- No conoce Spring, JPA, Kafka ni ningún framework
- Representa el QUE del negocio, no el COMO

```
domain/
└── model/
    └── Post.java        ← Entidad de negocio
```

### 2. Application

Define **lo que el sistema puede hacer** mediante puertos (interfaces) y orquesta los casos de uso.

- **Puertos de entrada** (`application/in`): interfaces que exponen las operaciones del sistema hacia afuera. El mundo exterior llama a estos puertos.
- **Puertos de salida** (`application/out`): interfaces que el sistema necesita del mundo exterior (ej: persistencia). El sistema llama a estos puertos.
- **Servicios** (`application/service`): implementan los puertos de entrada y usan los puertos de salida. Contienen la lógica de orquestación.

```
application/
├── in/
│   ├── CreatePostUseCase.java     ← Puerto de entrada (interface)
│   └── ListPostUseCase.java       ← Puerto de entrada (interface)
├── out/
│   └── PostRepository.java        ← Puerto de salida (interface)
└── service/
    ├── CreatePostService.java     ← Implementa CreatePostUseCase
    └── ListPostService.java       ← Implementa ListPostUseCase
```

> La capa Application NUNCA importa clases de Infrastructure.

### 3. Infrastructure

Contiene todos los **adaptadores**: implementaciones concretas de tecnologías externas que conectan el sistema con el mundo real.

- **Adaptadores de entrada** (`infrastructure/in`): reciben llamadas del exterior y las traducen a llamadas al puerto de entrada. Ejemplos: REST controllers, consumers de Kafka.
- **Adaptadores de salida** (`infrastructure/out`): implementan los puertos de salida usando tecnologías concretas. Ejemplos: repositorios JPA, clientes HTTP.

```
infraestructure/
├── in/
│   ├── PostController.java        ← Adaptador REST
│   └── web/
│       ├── CreatePostRequest.java ← DTO de entrada HTTP
│       ├── PostResponse.java      ← DTO de salida HTTP
│       └── PostWebMapper.java     ← Mapea Post ↔ PostResponse
└── out/
    ├── PostJpaAdapter.java        ← Implementa PostRepository (puerto)
    └── jpa/
        ├── PostJpaEntity.java     ← Entidad JPA
        ├── PostJpaRepository.java ← Spring Data JPA
        └── PostJpaMapper.java     ← Mapea Post ↔ PostJpaEntity
```

---

## Flujo de una peticion

### POST /api/posts/create

```
HTTP POST /api/posts/create
{ "title": "...", "content": "..." }
         │
         ▼
PostController               infrastructure/in     recibe el HTTP request
         │ llama a
         ▼
CreatePostUseCase            application/in        puerto de entrada (interface)
         │ implementado por
         ▼
CreatePostService            application/service   genera UUID, crea Post
         │ llama a
         ▼
PostRepository.save()        application/out       puerto de salida (interface)
         │ implementado por
         ▼
PostJpaAdapter               infrastructure/out    usa PostJpaMapper → PostJpaEntity
         │ usa
         ▼
PostJpaRepository.save()     infrastructure/out    Spring Data JPA
         │
         ▼
PostgreSQL → tabla posts
```

### GET /api/posts

```
HTTP GET /api/posts
         │
         ▼
PostController               infrastructure/in     recibe el HTTP request
         │ llama a
         ▼
ListPostUseCase              application/in        puerto de entrada (interface)
         │ implementado por
         ▼
ListPostService              application/service   delega al repositorio
         │ llama a
         ▼
PostRepository.findAll()     application/out       puerto de salida (interface)
         │ implementado por
         ▼
PostJpaAdapter               infrastructure/out    usa PostJpaMapper → List<Post>
         │ usa
         ▼
PostJpaRepository.findAll()  infrastructure/out    Spring Data JPA
         │
         ▼
PostgreSQL → tabla posts
         │
         ▼
PostWebMapper                infrastructure/in     mapea List<Post> → List<PostResponse>
         │
         ▼
HTTP Response: List<PostResponse>
```

---

## API Endpoints

### Crear un post

```bash
curl -X POST "http://localhost:8080/api/posts/create" -H "Content-Type: application/json" -d '{"title":"informacion","content":"que onda amigos"}'
```

### Listar todos los posts

```bash
curl -X GET "http://localhost:8080/api/posts"
```

### Respuesta esperada (GET)

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "informacion",
    "content": "que onda amigos"
  }
]
```

---

## Reglas de dependencia

| Capa | Puede importar | No puede importar |
|---|---|---|
| `domain` | Nada externo | Application, Infrastructure |
| `application` | `domain` | `infrastructure` |
| `infrastructure` | `application`, `domain` | Nada restringido |

La dependencia siempre apunta **hacia adentro**: Infrastructure → Application → Domain.

---

## Puertos y Adaptadores

| Tipo | Interface (Puerto) | Implementacion (Adaptador) |
|---|---|---|
| Entrada | `CreatePostUseCase` | `CreatePostService` |
| Entrada | `ListPostUseCase` | `ListPostService` |
| Salida | `PostRepository` | `PostJpaAdapter` |

---

## Estructura del proyecto

```
src/main/java/com/hexagonal/demo/
├── HexagonalDemoApplication.java
├── domain/
│   └── model/
│       └── Post.java
├── application/
│   ├── in/
│   │   ├── CreatePostUseCase.java
│   │   └── ListPostUseCase.java
│   ├── out/
│   │   └── PostRepository.java
│   └── service/
│       ├── CreatePostService.java
│       └── ListPostService.java
└── infraestructure/
    ├── in/
    │   ├── PostController.java
    │   └── web/
    │       ├── CreatePostRequest.java
    │       ├── PostResponse.java
    │       └── PostWebMapper.java
    └── out/
        ├── PostJpaAdapter.java
        └── jpa/
            ├── PostJpaEntity.java
            ├── PostJpaRepository.java
            └── PostJpaMapper.java

src/main/resources/
├── application.properties
└── db/migration/
    └── V1__create_posts_table.sql
```

---

## Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- Flyway (migraciones de base de datos)
- Lombok
- PostgreSQL (via Docker Compose)

---

## Beneficios de esta arquitectura

- **Testabilidad**: el dominio y los casos de uso se pueden testear sin levantar Spring ni la base de datos.
- **Intercambiabilidad**: se puede cambiar JPA por MongoDB sin tocar la lógica de negocio, solo reemplazando el adaptador.
- **Independencia de frameworks**: el negocio no conoce Spring.
- **Claridad**: cada clase tiene un rol claro y una ubicacion predecible.
