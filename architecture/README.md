# Software Architecture

Version in **[🇪 Español](README.sp.md)**

Practical implementations of architectural patterns focused on **maintainability**, **testability**, and **separation of concerns**. Each project is a runnable Spring Boot application with documented structure, flows, and design decisions.

---

## What You Will Find Here

This section explores how to structure backend applications beyond a traditional layered architecture. The goal is to keep business logic isolated from frameworks, databases, and delivery mechanisms (REST, messaging, CLI, etc.).

| Pattern | Folder | Status |
|---|---|---|
| Hexagonal Architecture (Ports & Adapters) | [`hexagonal/`](./hexagonal/) | In progress |

---

## Hexagonal Architecture

**Hexagonal Architecture**, also known as **Ports & Adapters**, was introduced by Alistair Cockburn. The core idea is simple: the business domain sits at the center and communicates with the outside world only through **ports** (interfaces) implemented by **adapters** (concrete technologies).

```
         Driving Adapters              Driven Adapters
    (REST, CLI, Kafka consumer)    (JPA, MongoDB, HTTP client)
                 │                              ▲
                 ▼                              │
          Input Ports                    Output Ports
         (Use Cases / In)               (Repository / Out)
                 │                              ▲
                 └──────────► Domain ◄───────────┘
```

### Why practice it here?

- **Test the core without Spring or a database** — mock ports instead of mocking the whole stack.
- **Swap technologies easily** — replace JPA with another store by changing only the output adapter.
- **Clear boundaries** — each package has a predictable role: `domain`, `application`, `infrastructure`.

---

## Projects

| Project | Folder | Documentation |
|---|---|---|
| **hexagonal-demo** — Posts | [`hexagonal/hexagonal-demo/`](./hexagonal/hexagonal-demo/) | **[English](./hexagonal/hexagonal-demo/README.md)** \| **[Español](./hexagonal/hexagonal-demo/README.sp.md)** |
| **hexagonal-demo-two** — Users | [`hexagonal/hexagonal-demo-two/`](./hexagonal/hexagonal-demo-two/) | **[English](./hexagonal/hexagonal-demo-two/README.md)** \| **[Español](./hexagonal/hexagonal-demo-two/README.sp.md)** |

Suggested learning path: start with **hexagonal-demo** (full diagrams and curl examples), then compare **hexagonal-demo-two** (alternative `port/in` layout).

---

## Resources

| Resource | Link |
|---|---|
| Hexagonal Architecture with Java & Spring (10 min) — *hexagonal-demo* | [Programando en JAVA](https://www.youtube.com/watch?v=PcoeGzGomqs) |
| Hexagonal Architecture + Spring Boot: Clean Design — *hexagonal-demo-two* | [Daniel Españadero — Nerdearla](https://www.youtube.com/watch?v=7MXjgfyTnYQ) |

---

## Back to the main learning path

**[System Design Learning — English](../README.md)** | **[Aprendizaje de Diseño de Sistemas — Español](../README.sp.md)**
