# Meetup RSVP — Spring Modulith Event Publication Demo

*Leer en español: [README.es.md](./README.es.md)*

A minimal Spring Modulith app built around one idea: the **event publication registry**. Every event published through Spring's `ApplicationEventPublisher` gets a row written to a database table (`event_publication`) *before* any listener runs. Listeners are marked complete when they finish successfully. If a listener was down, threw, or the app crashed mid-flight, the row stays **outstanding** (`completion_date IS NULL`) — and Spring Modulith replays it automatically the next time the app starts.

This repo is a hands-on companion to that mechanism: two modules, one event, one flag to force a failure, and the SQL to watch the registry do its job.

## Domain

- `events` — creates meetups and RSVPs. Publishes `RsvpReceived`.
- `notifications` — listens for `RsvpReceived` and writes a `Confirmation` row.

Creating a meetup or RSVP-ing is a plain write. The interesting part is what happens *after* the RSVP is saved: the service publishes an event, and everything downstream of that (the notification) goes through the registry instead of a direct method call.

## Prerequisites

- Java 25
- Docker (Postgres comes up via the bundled `compose.yaml`, no manual setup needed)

## Quickstart

```bash
./mvnw spring-boot:run
```

Spring Boot's docker-compose support starts Postgres automatically (host port `5455`, see `compose.yaml`). The app listens on `:8080`.

```bash
# create a meetup
curl -X POST http://localhost:8080/meetups \
  -H 'Content-Type: application/json' \
  -d '{"title":"Estudio de Java","date":"2026-05-15T18:00:00"}'

# RSVP (use the id returned above)
curl -X POST http://localhost:8080/meetups/1/rsvp \
  -H 'Content-Type: application/json' \
  -d '{"name":"Dan","email":"dan@example.com"}'
```

`date` is a `LocalDateTime`, so it needs the time component (`T18:00:00`). See [`client.http`](./client.http) for the request in IntelliJ HTTP Client form.

## The `event_publication` table

This is the table Spring Modulith uses as its event outbox / registry. It gets created automatically because `spring.modulith.events.jdbc.schema-initialization.enabled=true` is set in `application.yml` — nothing in `schema.sql` creates it.

```sql
create table public.event_publication
(
    id                     uuid                     not null
        primary key,
    listener_id            text                     not null,
    event_type             text                     not null,
    serialized_event       text                     not null,
    publication_date       timestamp with time zone not null,
    completion_date        timestamp with time zone,
    status                 text,
    completion_attempts    integer,
    last_resubmission_date timestamp with time zone
);

create index event_publication_serialized_event_hash_idx
    on public.event_publication using hash (serialized_event);

create index event_publication_by_completion_date_idx
    on public.event_publication (completion_date);
```

### Column by column

| Column | What it's for |
|---|---|
| `id` | Primary key for the publication record itself (not the event). |
| `listener_id` | Identifies **which listener** this row is for. Spring Modulith writes **one row per listener**, not one row per event — if two components listened to `RsvpReceived`, you'd get two rows per RSVP, each tracked independently. |
| `event_type` | Fully-qualified class name of the event, e.g. `com.tutorial.two.modulith.events.RsvpReceived`. |
| `serialized_event` | The event payload, serialized to JSON (Jackson by default) so it can be replayed later without the original in-memory object. |
| `publication_date` | When the event was published, i.e. right before the listener was invoked. |
| `completion_date` | **The field the whole mechanism hinges on.** `null` = still outstanding / not yet handled. Non-null = the listener finished without throwing. |
| `status` | Publication status (`PUBLISHED`, `COMPLETED`, etc. depending on Modulith version/config). |
| `completion_attempts` | How many times a retry/republish has been attempted for this row. |
| `last_resubmission_date` | Timestamp of the most recent replay attempt, if any. |

### How "checking if the event exists" actually works

There's no polling loop that scans for new events. The flow is transactional and synchronous with the write:

1. `MeetupService.rsvp(...)` saves the `Rsvp` row and calls `publisher.publishEvent(new RsvpReceived(...))` — a normal Spring `ApplicationEvent`, nothing Modulith-specific here.
2. Spring Modulith's `EventPublicationRegistry` intercepts this via an `ApplicationListener` at high priority. For every method annotated `@ApplicationModuleListener` that's subscribed to that event type, it **inserts a row** into `event_publication` with `completion_date = null`, in the *same* transaction as the RSVP save.
3. `@ApplicationModuleListener` on `NotificationListener.on(RsvpReceived)` expands to `@Async` + `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Transactional(propagation = REQUIRES_NEW)`. So the listener only fires **after** the RSVP transaction commits — the registry row and the RSVP row are guaranteed to exist together, or not at all.
4. If the listener returns normally, Modulith updates that row's `completion_date` (and `status`) to mark it complete.
5. If the listener throws (see `notifications.fail: true` below), nothing updates the row — it just sits there with `completion_date IS NULL`. The listener also doesn't retry immediately; it's now "outstanding" and waits for the next opportunity to be replayed.
6. On startup, because `republish-outstanding-events-on-restart: true` is set, Modulith queries `event_publication` for rows where `completion_date IS NULL`, deserializes `serialized_event` back into a `RsvpReceived` instance, and re-invokes the matching listener(s) by `listener_id`. If it now succeeds, `completion_date` gets filled in.

This is why the `hash` index exists on `serialized_event` (fast dedup/lookup by payload) and why there's a plain b-tree index on `completion_date` — that's exactly the column filtered on to find outstanding work, both during replay and for any completion housekeeping.

In short: "checking if an event exists" isn't a scan you write — it's `SELECT ... FROM event_publication WHERE completion_date IS NULL`, run by Modulith itself at startup (and optionally on a schedule, depending on configuration).

## Project structure

Two modules under `com.tutorial.two.modulith`, each with its own `internal/` package. Cross-module communication happens **only** through the published event — no module reaches into another's internals.

```
com.tutorial.two.modulith
├── SpringModulithTwoApplication.java
├── events/                          ← module 1: publishes RsvpReceived
│   ├── Meetup.java                  ← public (returned by controller)
│   ├── Rsvp.java                    ← public (returned by controller)
│   ├── RsvpReceived.java            ← public event
│   └── internal/
│       ├── MeetupController.java
│       ├── MeetupService.java
│       ├── MeetupRepository.java
│       └── RsvpRepository.java
└── notifications/                   ← module 2: zero public types
    └── internal/
        ├── Confirmation.java
        ├── ConfirmationRepository.java
        └── NotificationListener.java   (package-private, @ApplicationModuleListener)
```

The `notifications` module's only contract with the rest of the system is "I subscribe to `RsvpReceived`."

## The demo flow

### Happy path

1. `POST /meetups` → row in `meetup`.
2. `POST /meetups/{id}/rsvp` → row in `rsvp`, then `NotificationListener` writes a row in `confirmation`.
3. The event's registry row is completed:

```sql
SELECT * FROM confirmation;
SELECT id, event_type, listener_id, completion_date FROM event_publication ORDER BY publication_date DESC;
```

### Failure beat

1. Stop the app, set `notifications.fail: true` in `application.yml`, restart.
2. `POST` another RSVP. The listener throws before saving; no `confirmation` row is written.
3. The event is now an **outstanding publication**:

```sql
SELECT id, event_type, publication_date
FROM event_publication
WHERE completion_date IS NULL;
```

### Replay beat

1. Stop the app, set `notifications.fail: false`, restart.
2. `spring.modulith.events.republish-outstanding-events-on-restart=true` (already in `application.yml`) triggers a replay of every outstanding row on startup.
3. The `confirmation` row appears, and `completion_date` on that row gets filled in.

This is the punchline: at-least-once delivery for in-process events, for free, without hand-rolling an outbox table or a retry loop.

## Key configuration (`application.yml`)

```yaml
spring:
  application:
    name: meetup
  sql:
    init:
      mode: always
  modulith:
    events:
      jdbc:
        schema-initialization:
          enabled: true                        # auto-create event_publication
      republish-outstanding-events-on-restart: true   # the replay magic

notifications:
  fail: false                                  # flip to true for the failure demo
```

`meetup`, `rsvp`, `confirmation` come from `src/main/resources/schema.sql` (Spring Data JDBC has no auto-DDL, so `spring.sql.init.mode=always` runs it on every boot). `event_publication` is **not** in `schema.sql` — it's created by Modulith itself.

If you'd rather manage `event_publication` yourself (production, migrations via Flyway/Liquibase, etc.), the canonical DDL per database is in the Spring Modulith docs: [Schemas Appendix](https://docs.spring.io/spring-modulith/reference/appendix.html#schemas). Drop the `schema-initialization` property and run that SQL through your migration tool instead.

## Tests

```bash
./mvnw test
```

`SpringModulithTwoApplicationTests` does two things:

- `contextLoads()` — boots the full Spring context (a real Postgres, started by Boot's docker-compose support).
- `verifiesModuleStructure()` — runs `ApplicationModules.of(SpringModulithTwoApplication.class)`, Modulith's structural check. Catches any future cross-module reach into another module's `internal/` package at build time.

## Stack

- Java 25
- Spring Boot 4.0.6
- Spring Modulith 2.0.6
- Postgres (via Spring Boot Docker Compose support, `compose.yaml`, host port `5455`)
