# Embabel y Agentes de IA

Versión en **[🇺🇸 English](README.md)**

Aprendizaje práctico con **[Embabel Agent](https://github.com/embabel/embabel-agent)** — un framework de agentes para la JVM que combina Spring Boot, tipado fuerte y planificación orientada a objetivos para construir flujos con IA en Java.

---

## ¿Qué es Embabel?

**Embabel** (se pronuncia *Em-BAY-bel*) es un framework open source de agentes para la JVM creado por el equipo de Rod Johnson. Permite definir **agentes** compuestos por **acciones** y **objetivos** usando Java (o Kotlin) puro, con integración Spring Boot y soporte para múltiples proveedores de LLM.

En lugar de encadenar prompts manualmente, Embabel infiere un plan de ejecución a partir de tus tipos y anotaciones — tú te enfocas en objetos de dominio (`BlogDraft`, `ReviewedPost`) y el framework orquesta el flujo.

### Conceptos clave

| Concepto | Rol |
|---|---|
| `@Agent` | Marca una clase como agente que Embabel puede descubrir y ejecutar |
| `@Action` | Un paso que el agente puede realizar (llama a un LLM, ejecuta lógica, etc.) |
| `@AchievesGoal` | Marca la acción que completa el objetivo del agente |
| Objetos de dominio | Records/clases tipados que fluyen entre acciones (el *blackboard*) |
| `Ai` | API para invocar LLMs con personas, roles y salida estructurada |

### ¿Por qué usarlo?

Según la [guía oficial](https://docs.embabel.com/embabel-agent/guide/0.5.0-SNAPSHOT/):

- **Planificación sofisticada** — planificación orientada a objetivos (GOAP) en lugar de pipelines rígidos
- **Tipado fuerte** — entradas/salidas estructuradas con records de Java, no JSON suelto
- **Integración Spring & JVM** — encaja de forma natural en apps Spring Boot existentes
- **Mezcla de LLMs** — distintos modelos por acción o rol (escritor vs. revisor)
- **Diseñado para testabilidad** — las acciones son métodos Java, fáciles de testear

---

## Recursos oficiales

| Recurso | Enlace |
|---|---|
| Repositorio GitHub | [github.com/embabel/embabel-agent](https://github.com/embabel/embabel-agent) |
| Guía de usuario (0.5.0-SNAPSHOT) | [docs.embabel.com/embabel-agent/guide/0.5.0-SNAPSHOT/](https://docs.embabel.com/embabel-agent/guide/0.5.0-SNAPSHOT/) |
| Repositorio Maven | [repo.embabel.com](https://repo.embabel.com/) |

---

## Tutorial de Dan Vega — Blog Agent

| Recurso | Enlace |
|---|---|
| Mi proyecto | **[blog-agent (Español)](./blog-agent/README.sp.md)** \| **[blog-agent (English)](./blog-agent/README.md)** |
| Proyecto de Dan Vega | [github.com/danvega/blog-agent](https://github.com/danvega/blog-agent) |
| Video de YouTube | [Building a Blog Agent with Embabel](https://www.youtube.com/watch?v=G5VDQCZu6t0) |

---

## Volver a la ruta principal de aprendizaje

**[Aprendizaje de Diseño de Sistemas — Español](../../README.sp.md)** | **[System Design Learning — English](../../README.md)**
