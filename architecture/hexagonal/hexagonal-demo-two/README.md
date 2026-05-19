# Hexagonal Architecture Demo — Users

Version in **[🇪 Español](README.sp.md)**

User registration and lookup REST API. Same hexagonal principles with ports organized under `application/port/in` and `application/port/out`.

---

## Project structure

```
architecture/hexagonal/hexagonal-demo-two/
├── domain/model/User.java
├── application/port/in/     ← CreateUserUseCase, GetUserUseCase
├── application/port/out/    ← UserRepositoryPort
├── application/service/     ← UserService
└── infraestructure/         ← UserController + JPA adapter
```

---

## Tech stack

- Java 21
- Spring Boot 4
- Spring Data JPA
- PostgreSQL (Docker Compose)
- Lombok

---

## Prerequisites

- JDK 21+
- Docker (for PostgreSQL via `compose.yaml`)

---

## Run

From the project root:

```bash
cd architecture/hexagonal/hexagonal-demo-two
docker compose up -d
./mvnw spring-boot:run
```

---

## API endpoints

### Create a user

```bash
curl -X POST "http://localhost:8080/users" \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Ada","lastName":"Lovelace"}'
```

### Get a user by id

```bash
curl -X GET "http://localhost:8080/users/1"
```

---

## Request flow

```
HTTP POST /users
       │
       ▼
UserController              infrastructure/controller
       │ calls
       ▼
CreateUserUseCase             application/port/in
       │ implemented by
       ▼
UserService                   application/service
       │ calls
       ▼
UserRepositoryPort            application/port/out
       │ implemented by
       ▼
JpaUserRepositoryAdapter      infrastructure/persistence
       │
       ▼
PostgreSQL
```

---

## Ports and adapters

| Type | Port (interface) | Adapter |
|---|---|---|
| Input | `CreateUserUseCase` | `UserService` |
| Input | `GetUserUseCase` | `UserService` |
| Output | `UserRepositoryPort` | `JpaUserRepositoryAdapter` |

---

## Layout vs hexagonal-demo

This project uses an alternative package naming convention compared to [hexagonal-demo](../hexagonal-demo/README.md):

| hexagonal-demo | hexagonal-demo-two |
|---|---|
| `application/in` | `application/port/in` |
| `application/out` | `application/port/out` |

Both follow the same dependency rule: **Infrastructure → Application → Domain**.

---

## Back to Architecture

**[Architecture overview (English)](../../README.md)** | **[Visión general (Español)](../../README.sp.md)**
