# Blog Agent

Versión en **[🇺🇸 English](README.md)**

App Spring Boot que escribe y revisa posts en Markdown usando el framework [Embabel Agent](https://github.com/embabel/embabel-agent).

Esta sección sigue el tutorial de **[Dan Vega](https://www.danvega.dev/)** sobre cómo construir un agente de escritura de blogs con Embabel y Spring Boot.

| Recurso | Enlace |
|---|---|
| Video de YouTube | [Building a Blog Agent with Embabel](https://www.youtube.com/watch?v=G5VDQCZu6t0) |
| Artículo de Dan Vega | [Embabel First Look](https://www.danvega.dev/blog/embabel-first-look) |
| Repositorio original | [github.com/danvega/blog-agent](https://github.com/danvega/blog-agent) |

El ejemplo completo de Dan incluye un pipeline de cinco etapas (investigación → borrador → revisión → TLDR → front matter) con búsqueda web via MCP. Este repositorio implementa una **versión simplificada** centrada en el flujo de escribir y revisar.

---

## blog-agent — código fuente local

Mi implementación basada en el tutorial de Dan Vega. Dos acciones:

1. **`writeDaft`** — genera un `BlogDraft` con el LLM por defecto y la persona de escritor
2. **`reviewDraft`** — revisa el borrador con el rol de revisor, produce un `ReviewedPost` y lo guarda en disco

```
ia/embabel/blog-agent/
├── src/main/java/com/manuonda/blog/agent/
│   ├── BlogWriterAgent.java    ← @Agent con métodos @Action
│   ├── Personas.java           ← RoleGoalBackstory para escritor y revisor
│   ├── BlogDraft.java          ← record de dominio (title, content)
│   ├── ReviewedPost.java       ← record de dominio (title, content, feedback)
│   └── BlogAgentProperties.java
├── src/main/resources/application.yaml
└── blog-posts/                 ← archivos Markdown generados
```

---

## Stack tecnológico

- Java 23
- Spring Boot 3.5
- Embabel Agent `0.5.0-SNAPSHOT`
- Integración Spring AI OpenAI
- Shell interactivo de Embabel

---

## Requisitos previos

- JDK 23+
- [API key de OpenAI](https://platform.openai.com/) exportada como `OPENAI_API_KEY`

---

## Ejecución

```bash
cd ia/embabel/blog-agent
export OPENAI_API_KEY=tu-clave-aqui
./mvnw spring-boot:run
```

En el shell de Embabel, invoca el agente con un tema:

```bash
x "Introducción a Pikachu para principiantes"
```

El post revisado se guarda en `blog-posts/` como archivo Markdown.

---

## Configuración

Ajustes clave en `application.yaml`:

```yaml
blog-agent:
  output-dir: blog-posts

embabel:
  agent:
    platform:
      models:
        openai:
          api-key: ${OPENAI_API_KEY}
  models:
    default-llm: gpt-4.1-mini
    llms:
      reviewer: gpt-4.1-mini
```

---

## Flujo del agente

```
Entrada del usuario (tema)
       │
       ▼
  writeDaft()          ← LLM por defecto + persona WRITER
       │
       ▼
   BlogDraft           ← title + content (Markdown)
       │
       ▼
  reviewDraft()        ← LLM revisor + persona REVIEWER  [@AchievesGoal]
       │
       ▼
  ReviewedPost         ← título revisado, contenido, feedback
       │
       ▼
  blog-posts/*.md      ← guardado en disco
```

---

## Volver a Embabel

**[Visión general Embabel (Español)](../README.sp.md)** | **[Embabel overview (English)](../README.md)**
