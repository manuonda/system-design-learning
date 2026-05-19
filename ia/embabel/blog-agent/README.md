# Blog Agent

Version in **[🇪 Español](README.sp.md)**

Spring Boot app that writes and reviews Markdown blog posts using the [Embabel Agent](https://github.com/embabel/embabel-agent) framework.

This section follows **[Dan Vega](https://www.danvega.dev/)**'s tutorial on building a blog-writing agent with Embabel and Spring Boot.

| Resource | Link |
|---|---|
| YouTube video | [Building a Blog Agent with Embabel](https://www.youtube.com/watch?v=G5VDQCZu6t0) |
| Dan Vega blog post | [Embabel First Look](https://www.danvega.dev/blog/embabel-first-look) |
| Original repository | [github.com/danvega/blog-agent](https://github.com/danvega/blog-agent) |

Dan's full example includes a five-stage pipeline (research → draft → review → TLDR → front matter) with MCP web search. This repository implements a **simplified version** focused on the write-and-review flow.

---

## blog-agent — local source code

My implementation based on Dan Vega's tutorial. Two actions:

1. **`writeDaft`** — generates a `BlogDraft` using the default LLM and a writer persona
2. **`reviewDraft`** — reviews the draft with a reviewer role, produces a `ReviewedPost`, and saves it to disk

```
ia/embabel/blog-agent/
├── src/main/java/com/manuonda/blog/agent/
│   ├── BlogWriterAgent.java    ← @Agent with @Action methods
│   ├── Personas.java           ← RoleGoalBackstory for writer & reviewer
│   ├── BlogDraft.java          ← domain record (title, content)
│   ├── ReviewedPost.java       ← domain record (title, content, feedback)
│   └── BlogAgentProperties.java
├── src/main/resources/application.yaml
└── blog-posts/                 ← generated Markdown files
```

---

## Tech stack

- Java 23
- Spring Boot 3.5
- Embabel Agent `0.5.0-SNAPSHOT`
- Spring AI OpenAI integration
- Embabel interactive shell

---

## Prerequisites

- JDK 23+
- [OpenAI API key](https://platform.openai.com/) exported as `OPENAI_API_KEY`

---

## Run

```bash
cd ia/embabel/blog-agent
export OPENAI_API_KEY=your-key-here
./mvnw spring-boot:run
```

In the Embabel shell, invoke the agent with a topic:

```bash
x "Introduction to Pikachu for beginners"
```

The reviewed post is saved under `blog-posts/` as a Markdown file.

---

## Configuration

Key settings in `application.yaml`:

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

## Agent flow

```
User input (topic)
       │
       ▼
  writeDaft()          ← default LLM + WRITER persona
       │
       ▼
   BlogDraft           ← title + content (Markdown)
       │
       ▼
  reviewDraft()        ← reviewer LLM + REVIEWER persona  [@AchievesGoal]
       │
       ▼
  ReviewedPost         ← revised title, content, feedback
       │
       ▼
  blog-posts/*.md      ← saved to disk
```

---

## Back to Embabel

**[Embabel overview (English)](../README.md)** | **[Visión general Embabel (Español)](../README.sp.md)**
