# Embabel & AI Agents

Version in **[🇪 Español](README.sp.md)**

Hands-on learning with **[Embabel Agent](https://github.com/embabel/embabel-agent)** — an agent framework for the JVM that combines Spring Boot, strong typing, and goal-oriented planning to build AI-powered workflows in Java.

---

## What is Embabel?

**Embabel** (pronounced *Em-BAY-bel*) is an open-source agent framework for the JVM created by Rod Johnson's team. It lets you define **agents** made of **actions** and **goals** using plain Java (or Kotlin), with Spring Boot integration and support for multiple LLM providers.

Instead of chaining prompts manually, Embabel infers an execution plan from your types and annotations — you focus on domain objects (`BlogDraft`, `ReviewedPost`) and the framework orchestrates the flow.

### Core concepts

| Concept | Role |
|---|---|
| `@Agent` | Marks a class as an agent that Embabel can discover and run |
| `@Action` | A step the agent can perform (calls an LLM, runs logic, etc.) |
| `@AchievesGoal` | Marks the action that completes the agent's objective |
| Domain objects | Typed records/classes that flow between actions (the *blackboard*) |
| `Ai` | API to invoke LLMs with personas, roles, and structured output |

### Why use it?

From the [official guide](https://docs.embabel.com/embabel-agent/guide/0.5.0-SNAPSHOT/):

- **Sophisticated planning** — goal-oriented action planning (GOAP) instead of hard-coded pipelines
- **Strong typing** — structured inputs/outputs with Java records, not loose JSON
- **Spring & JVM integration** — fits naturally into existing Spring Boot apps
- **LLM mixing** — different models per action or role (writer vs. reviewer)
- **Designed for testability** — actions are plain methods, easy to unit test

---

## Official resources

| Resource | Link |
|---|---|
| GitHub repository | [github.com/embabel/embabel-agent](https://github.com/embabel/embabel-agent) |
| User guide (0.5.0-SNAPSHOT) | [docs.embabel.com/embabel-agent/guide/0.5.0-SNAPSHOT/](https://docs.embabel.com/embabel-agent/guide/0.5.0-SNAPSHOT/) |
| Maven repository | [repo.embabel.com](https://repo.embabel.com/) |

---

## Dan Vega tutorial — Blog Agent

| Resource | Link |
|---|---|
| My project | **[blog-agent (English)](./blog-agent/README.md)** \| **[blog-agent (Español)](./blog-agent/README.sp.md)** |
| Dan Vega project | [github.com/danvega/blog-agent](https://github.com/danvega/blog-agent) |
| YouTube video | [Building a Blog Agent with Embabel](https://www.youtube.com/watch?v=G5VDQCZu6t0) |

---

## Back to the main learning path

**[System Design Learning — English](../../README.md)** | **[Aprendizaje de Diseño de Sistemas — Español](../../README.sp.md)**
