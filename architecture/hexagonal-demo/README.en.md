# Hexagonal Architecture Demo

Demonstration project of **Hexagonal Architecture** (Ports & Adapters) with Spring Boot and Java 21.

---

## Installation and build

From the project root (where `pom.xml` is):

```bash
mvn clean install -DskipTests
```

## Run

```bash
./mvnw spring-boot:run
```

---

## What is Hexagonal Architecture

Hexagonal Architecture, also known as **Ports & Adapters**, was proposed by Alistair Cockburn. Its main goal is to **isolate the business core** from external technological details (databases, frameworks, messaging, REST APIs, etc.).

The central idea is that the business should not know whether it is being called from a REST API, a Kafka consumer, or a unit test. Nor should it know whether data is stored in PostgreSQL, MongoDB, or in memory.

---

## Architecture Diagram

```
                    ┌─────────────────────────────────────────┐
                    │            OUTSIDE WORLD                 │
                    │   HTTP Client / Postman / curl           │
                    └──────────────────┬──────────────────────┘
                                       │
                    ┌──────────────────▼──────────────────────┐
                    │         INFRASTRUCTURE / IN              │
                    │          (Driving Adapters)              │
                    │                                          │
                    │  ┌─────────────────────────────────┐    │
                    │  │  PostController  @RestController │    │
                    │  │  POST /api/posts/create          │    │
                    │  │  GET  /api/posts                 │    │
                    │  └────────────┬────────────────┬────┘    │
                    │               │                │         │
                    │  ┌────────────▼────┐  ┌────────▼──────┐ │
                    │  │CreatePostRequest│  │ PostWebMapper │ │
                    │  │  (input DTO)    │  │ PostResponse  │ │
                    │  └─────────────────┘  └───────────────┘ │
                    └──────────────────┬──────────────────────┘
                                       │ uses ports (interfaces)
                    ┌──────────────────▼──────────────────────┐
                    │            APPLICATION / IN              │
                    │            (Input Ports)                 │
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
                    │                    │ implemented by      │
                    │          APPLICATION / SERVICE           │
                    │                    │                     │
                    │  ┌─────────────────▼────────────────┐   │
                    │  │  CreatePostService  @Service     │   │
                    │  │  ListPostService    @Service     │   │
                    │  │  - postRepository (interface)    │   │
                    │  └─────────────────┬────────────────┘   │
                    │                    │ uses output port    │
                    │           APPLICATION / OUT              │
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
                                       │ implemented by        │
                    ┌──────────────────▼──────────────────────┐
                    │         INFRASTRUCTURE / OUT             │
                    │          (Driven Adapters)               │
                    │                                          │
                    │  ┌───────────────────────────────────┐  │
                    │  │  PostJpaAdapter  @Component       │  │
                    │  │  implements PostRepository        │  │
                    │  │  - postJpaRepository              │  │
                    │  │  - postJpaMapper                  │  │
                    │  └────────────┬──────────────────────┘  │
                    │               │ uses                     │
                    │  ┌────────────▼──────────────────────┐  │
                    │  │  PostJpaMapper   @Component       │  │
                    │  │  Post ↔ PostJpaEntity             │  │
                    │  └────────────┬──────────────────────┘  │
                    │               │ uses                     │
                    │  ┌────────────▼──────────────────────┐  │
                    │  │  PostJpaRepository                │  │
                    │  │  extends JpaRepository            │  │
                    │  │  <PostJpaEntity, String>          │  │
                    │  └────────────┬──────────────────────┘  │
                    │               │ maps to                  │
                    │  ┌────────────▼──────────────────────┐  │
                    │  │  PostJpaEntity  @Entity           │  │
                    │  │  @Table("posts")                  │  │
                    │  │  id, title, content               │  │
                    │  └───────────────────────────────────┘  │
                    └──────────────────┬──────────────────────┘
                                       │
                    ┌──────────────────▼──────────────────────┐
                    │              PostgreSQL                  │
                    │  table: posts                           │
                    │  Flyway: V1__create_posts_table.sql     │
                    └─────────────────────────────────────────┘

                    ┌─────────────────────────────────────────┐
                    │               DOMAIN                     │
                    │    (Core — no external dependencies)     │
                    │                                          │
                    │   Post.java  @Data                       │
                    │   - id: String                           │
                    │   - title: String                        │
                    │   - content: String                      │
                    └─────────────────────────────────────────┘

  Dependency rule:
  Infrastructure ──► Application ──► Domain   (never the other way around)
```

---

## The 3 Layers

### 1. Domain

The heart of the system. Contains **business entities** and pure domain logic.

- Does not depend on any external library
- Does not know Spring, JPA, Kafka or any framework
- Represents the WHAT of the business, not the HOW

```
domain/
└── model/
    └── Post.java        ← Business entity
```

### 2. Application

Defines **what the system can do** through ports (interfaces) and orchestrates use cases.

- **Input ports** (`application/in`): interfaces that expose the system's operations to the outside. The outside world calls these ports.
- **Output ports** (`application/out`): interfaces that the system needs from the outside world (e.g. persistence). The system calls these ports.
- **Services** (`application/service`): implement input ports and use output ports. They contain the orchestration logic.

```
application/
├── in/
│   ├── CreatePostUseCase.java     ← Input port (interface)
│   └── ListPostUseCase.java       ← Input port (interface)
├── out/
│   └── PostRepository.java        ← Output port (interface)
└── service/
    ├── CreatePostService.java     ← Implements CreatePostUseCase
    └── ListPostService.java       ← Implements ListPostUseCase
```

> The Application layer NEVER imports classes from Infrastructure.

### 3. Infrastructure

Contains all **adapters**: concrete implementations of external technologies that connect the system to the real world.

- **Driving adapters** (`infrastructure/in`): receive calls from outside and translate them into calls to the input port. Examples: REST controllers, Kafka consumers.
- **Driven adapters** (`infrastructure/out`): implement output ports using concrete technologies. Examples: JPA repositories, HTTP clients, Kafka producers.

```
infraestructure/
├── in/
│   ├── PostController.java        ← REST adapter
│   └── web/
│       ├── CreatePostRequest.java ← HTTP input DTO
│       ├── PostResponse.java      ← HTTP output DTO
│       └── PostWebMapper.java     ← Maps Post ↔ PostResponse
└── out/
    ├── PostJpaAdapter.java        ← Implements PostRepository (port)
    └── jpa/
        ├── PostJpaEntity.java     ← JPA entity
        ├── PostJpaRepository.java ← Spring Data JPA
        └── PostJpaMapper.java     ← Maps Post ↔ PostJpaEntity
```

---

## Request Flow

### POST /api/posts/create

```
HTTP POST /api/posts/create
{ "title": "...", "content": "..." }
         │
         ▼
PostController               infrastructure/in     receives the HTTP request
         │ calls
         ▼
CreatePostUseCase            application/in        input port (interface)
         │ implemented by
         ▼
CreatePostService            application/service   generates UUID, creates Post
         │ calls
         ▼
PostRepository.save()        application/out       output port (interface)
         │ implemented by
         ▼
PostJpaAdapter               infrastructure/out    uses PostJpaMapper → PostJpaEntity
         │ uses
         ▼
PostJpaRepository.save()     infrastructure/out    Spring Data JPA
         │
         ▼
PostgreSQL → posts table
```

### GET /api/posts

```
HTTP GET /api/posts
         │
         ▼
PostController               infrastructure/in     receives the HTTP request
         │ calls
         ▼
ListPostUseCase              application/in        input port (interface)
         │ implemented by
         ▼
ListPostService              application/service   delegates to repository
         │ calls
         ▼
PostRepository.findAll()     application/out       output port (interface)
         │ implemented by
         ▼
PostJpaAdapter               infrastructure/out    uses PostJpaMapper → List<Post>
         │ uses
         ▼
PostJpaRepository.findAll()  infrastructure/out    Spring Data JPA
         │
         ▼
PostgreSQL → posts table
         │
         ▼
PostWebMapper                infrastructure/in     maps List<Post> → List<PostResponse>
         │
         ▼
HTTP Response: List<PostResponse>
```

---

## API Endpoints

### Create a post

```bash
curl -X POST "http://localhost:8080/api/posts/create" -H "Content-Type: application/json" -d '{"title":"informacion","content":"que onda amigos"}'
```

### List all posts

```bash
curl -X GET "http://localhost:8080/api/posts"
```

### Expected response (GET)

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

## Dependency Rules

| Layer | Can import | Cannot import |
|---|---|---|
| `domain` | Nothing external | Application, Infrastructure |
| `application` | `domain` | `infrastructure` |
| `infrastructure` | `application`, `domain` | No restriction |

Dependencies always point **inward**: Infrastructure → Application → Domain.

---

## Ports and Adapters

| Type | Interface (Port) | Implementation (Adapter) |
|---|---|---|
| Input | `CreatePostUseCase` | `CreatePostService` |
| Input | `ListPostUseCase` | `ListPostService` |
| Output | `PostRepository` | `PostJpaAdapter` |

---

## Project Structure

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

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Flyway (database migrations)
- Lombok
- PostgreSQL (via Docker Compose)

---

## Benefits of this Architecture

- **Testability**: domain and use cases can be tested without starting Spring or the database.
- **Replaceability**: JPA can be replaced with MongoDB without touching business logic — just swap the adapter.
- **Framework independence**: the business core does not know Spring.
- **Clarity**: each class has a clear role and a predictable location.
