# Arquitectura de Software

Versión en **[🇺🇸 English](README.md)**

Implementaciones prácticas de patrones arquitectónicos orientados a la **mantenibilidad**, **testabilidad** y **separación de responsabilidades**. Cada proyecto es una aplicación Spring Boot ejecutable, con estructura, flujos y decisiones de diseño documentados.

---

## Qué encontrarás aquí

Esta sección explora cómo estructurar aplicaciones backend más allá de una arquitectura en capas tradicional. El objetivo es mantener la lógica de negocio aislada de frameworks, bases de datos y mecanismos de entrega (REST, mensajería, CLI, etc.).

| Patrón | Carpeta | Estado |
|---|---|---|
| Arquitectura Hexagonal (Ports & Adapters) | [`hexagonal/`](./hexagonal/) | En progreso |

---

## Arquitectura Hexagonal

La **Arquitectura Hexagonal**, también conocida como **Ports & Adapters**, fue introducida por Alistair Cockburn. La idea central es simple: el dominio de negocio queda en el centro y se comunica con el mundo exterior únicamente a través de **puertos** (interfaces) implementados por **adaptadores** (tecnologías concretas).

```
      Adaptadores de Entrada           Adaptadores de Salida
   (REST, CLI, consumer de Kafka)   (JPA, MongoDB, cliente HTTP)
                 │                              ▲
                 ▼                              │
          Puertos de Entrada             Puertos de Salida
         (Casos de uso / In)            (Repositorio / Out)
                 │                              ▲
                 └──────────► Domain ◄───────────┘
```

### ¿Por qué practicarla aquí?

- **Probar el núcleo sin Spring ni base de datos** — se mockean puertos en lugar de todo el stack.
- **Cambiar tecnologías con facilidad** — reemplazar JPA por otro almacén modificando solo el adaptador de salida.
- **Límites claros** — cada paquete tiene un rol predecible: `domain`, `application`, `infrastructure`.

---

## Proyectos

| Proyecto | Carpeta | Documentación |
|---|---|---|
| **hexagonal-demo** — Posts | [`hexagonal/hexagonal-demo/`](./hexagonal/hexagonal-demo/) | **[Español](./hexagonal/hexagonal-demo/README.sp.md)** \| **[English](./hexagonal/hexagonal-demo/README.md)** |
| **hexagonal-demo-two** — Usuarios | [`hexagonal/hexagonal-demo-two/`](./hexagonal/hexagonal-demo-two/) | **[Español](./hexagonal/hexagonal-demo-two/README.sp.md)** \| **[English](./hexagonal/hexagonal-demo-two/README.md)** |

Ruta sugerida: comienza con **hexagonal-demo** (diagramas completos y ejemplos curl), luego compara **hexagonal-demo-two** (layout alternativo con `port/in`).

---

## Recursos

| Recurso | Enlace |
|---|---|
| Arquitectura Hexagonal con Java y Spring (10 min) — *hexagonal-demo* | [Programando en JAVA](https://www.youtube.com/watch?v=PcoeGzGomqs) |
| Arquitectura Hexagonal + Spring Boot: Diseño Limpio — *hexagonal-demo-two* | [Daniel Españadero — Nerdearla](https://www.youtube.com/watch?v=7MXjgfyTnYQ) |

---

## Volver a la ruta principal de aprendizaje

**[Aprendizaje de Diseño de Sistemas — Español](../README.sp.md)** | **[System Design Learning — English](../README.md)**
