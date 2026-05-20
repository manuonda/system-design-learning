# Hexagonal Architecture Demo — Usuarios

Versión en **[🇺🇸 English](README.md)**

API REST para registrar y consultar usuarios. Mismos principios hexagonales con puertos organizados bajo `application/port/in` y `application/port/out`.

## Video de referencia

[Arquitectura Hexagonal + Spring Boot: Domina el Diseño Limpio](https://www.youtube.com/watch?v=7MXjgfyTnYQ) — Daniel Españadero, Nerdearla

---

## Estructura del proyecto

```
architecture/hexagonal/hexagonal-demo-two/
├── domain/model/User.java
├── application/port/in/     ← CreateUserUseCase, GetUserUseCase
├── application/port/out/    ← UserRepositoryPort
├── application/service/     ← UserService
└── infraestructure/         ← UserController + adaptador JPA
```

---

## Stack tecnológico

- Java 21
- Spring Boot 4
- Spring Data JPA
- PostgreSQL (Docker Compose)
- Lombok

---

## Requisitos previos

- JDK 21+
- Docker (para PostgreSQL via `compose.yaml`)

---

## Ejecución

Desde la raíz del proyecto:

```bash
cd architecture/hexagonal/hexagonal-demo-two
docker compose up -d
./mvnw spring-boot:run
```

---

## Endpoints de la API

### Crear un usuario

```bash
curl -X POST "http://localhost:8080/users" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ada","lastName":"Lovelace"}'
```

### Obtener un usuario por id

```bash
curl -X GET "http://localhost:8080/users/1"
```

---

## Flujo de una petición

```
HTTP POST /users
       │
       ▼
UserController              infrastructure/controller
       │ llama a
       ▼
CreateUserUseCase             application/port/in
       │ implementado por
       ▼
UserService                   application/service
       │ llama a
       ▼
UserRepositoryPort            application/port/out
       │ implementado por
       ▼
JpaUserRepositoryAdapter      infrastructure/persistence
       │
       ▼
PostgreSQL
```

---

## Puertos y adaptadores

| Tipo | Puerto (interface) | Adaptador |
|---|---|---|
| Entrada | `CreateUserUseCase` | `UserService` |
| Entrada | `GetUserUseCase` | `UserService` |
| Salida | `UserRepositoryPort` | `JpaUserRepositoryAdapter` |

---

## Layout vs hexagonal-demo

Este proyecto usa una convención de paquetes alternativa comparada con [hexagonal-demo](../hexagonal-demo/README.sp.md):

| hexagonal-demo | hexagonal-demo-two |
|---|---|
| `application/in` | `application/port/in` |
| `application/out` | `application/port/out` |

Ambos siguen la misma regla de dependencias: **Infrastructure → Application → Domain**.

---

## Volver a Arquitectura

**[Visión general (Español)](../../README.sp.md)** | **[Architecture overview (English)](../../README.md)**
