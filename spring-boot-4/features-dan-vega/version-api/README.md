# API Versioning in Spring Boot 4

> Learning project based on the new native API versioning capabilities introduced in **Spring Framework 7** and **Spring Boot 4**, inspired by the work of [Dan Vega](https://www.danvega.dev/).

---

## Table of Contents

- [Context](#context)
- [Tech Stack](#tech-stack)
- [What's New in Spring Framework 7](#whats-new-in-spring-framework-7)
- [Versioning Strategies](#versioning-strategies)
- [Project Structure](#project-structure)
- [How This Project Works](#how-this-project-works)
- [Configuration](#configuration)
- [Endpoints](#endpoints)
- [Testing](#testing)
- [How to Run](#how-to-run)
- [References](#references)

---

## Context

Before Spring Framework 7, versioning a REST API in Spring required manual solutions or third-party libraries:

- Manually defining paths with `/v1/`, `/v2/`
- Using custom conditions in `@RequestMapping`
- Delegating routing to an external API Gateway

**Spring Framework 7** introduces native versioning support directly in MVC, eliminating that boilerplate and offering a declarative, configurable strategy.

---

## Tech Stack

| Technology       | Version |
|------------------|---------|
| Java             | 25      |
| Spring Boot      | 4.0.5   |
| Spring Framework | 7.x     |
| Spring MVC       | Built-in API Versioning |
| Maven            | 3.x     |

---

## What's New in Spring Framework 7

Spring Framework 7 adds native API versioning support in Spring MVC via:

### `ApiVersionConfigurer` (new in Spring MVC)

Configured via `WebMvcConfigurer.configureApiVersioning()`, it allows defining:

- Supported versions: `addSupportedVersions("1.0", "2.0")`
- Default version: `setDefaultVersion("1.0")`
- Version resolution strategy

### Version resolution strategies

| Strategy           | Configuration method          | Example                          |
|--------------------|-------------------------------|----------------------------------|
| Path segment       | `usePathSegment(int pos)`     | `GET /api/v1/users`              |
| Request header     | `useRequestHeader(String)`    | `X-API-Version: 1.0`             |
| Query parameter    | `useQueryParam(String)`       | `GET /api/users?version=1.0`     |
| Media type param   | `useMediaTypeParam(String)`   | `Accept: application/json;version=1` |

### `version` attribute in mappings

The `version` attribute can be used directly in `@GetMapping`, `@PostMapping`, etc.:

```java
@GetMapping(value = "/{version}/users", version = "1.0")
public List<UserDTOv1> getUsers() { ... }

@GetMapping(value = "/{version}/users", version = "2.0")
public List<UserDTOv2> getUsersV2() { ... }
```

Spring automatically resolves the correct handler based on the version detected in the request.

---

## Versioning Strategies

### 1. Path Segment (used in this project — Strategy A)

The version is embedded in the URL path as a segment:

```
GET /api/v1/users
GET /api/v2/users
```

**Pros:** Simple, visible, cacheable, easy to test with curl/Postman.  
**Cons:** Breaks strict REST principles (the URI should identify the resource, not its version).

### 2. Request Header (Strategy B)

```
GET /api/users
X-API-Version: 2.0
```

**Pros:** Clean URI.  
**Cons:** Less visible, requires client configuration.

### 3. Query Parameter

```
GET /api/users?version=2.0
```

**Pros:** Easy to test in the browser.  
**Cons:** Unconventional for REST.

### 4. Media Type Parameter (Content Negotiation)

```
Accept: application/json;version=2
```

**Pros:** Follows the HTTP content negotiation standard.  
**Cons:** More complex to configure and document.

---

## Project Structure

```
src/main/java/com/spring4/version/
├── VersionApiApplication.java      # Entry point
├── User.java                       # Domain entity (record)
├── UserRepository.java             # In-memory repository
├── UserMapper.java                 # Maps User → DTOs
├── UserDTOv1.java                  # DTO version 1.0 (id, name, email, webSite)
├── UserDTOv2.java                  # DTO version 2.0 (id, name, email, lastName)
├── UserVersionController.java      # Strategy A: Path Segment (/api/{version}/users)
├── UserHeaderController.java       # Strategy B: Request Header (/header/users)
└── config/
    ├── WebConfig.java              # Active API versioning configuration
    └── ApiVersionParser.java       # Normalizes "v1", "1" → "1.0"

src/test/java/com/spring4/version/
├── UserApiVersionTest.java         # Integration tests — path segment strategy
└── UserHeaderControllerTest.java   # Unit tests — request header strategy (@WebMvcTest)
```

---

## How This Project Works

### Domain

`User` is a Java Record with validation in the compact constructor:

```java
public record User(Integer id, String name, String email, String webSite) {
    public User {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }
}
```

### DTOs per version

**v1** — basic fields:
```java
public record UserDTOv1(Integer id, String name, String email, String webSite) {}
```

**v2** — adds `lastName` (split from `name` as a demo):
```java
public record UserDTOv2(Integer id, String name, String email, String lastName) {}
```

### Mapper

`UserMapper` converts the domain to each DTO version:

```java
@Component
public class UserMapper {

    public UserDTOv1 toV1(User user) {
        return new UserDTOv1(user.id(), user.name(), user.email(), user.webSite());
    }

    public UserDTOv2 toV2(User user) {
        String[] parts = user.name().split(" ");
        return new UserDTOv2(user.id(), parts[0], user.email(),
                parts.length > 1 ? parts[1] : "");
    }
}
```

### Controller

```java
@RestController
@RequestMapping("/api")
public class UserVersionController {

    @GetMapping(value = "/{version}/users", version = "1.0")
    public List<UserDTOv1> getUsers() { ... }

    @GetMapping(value = "/{version}/users", version = "1.1")
    public List<UserDTOv1> getUsers1_1() { ... }

    @GetMapping(value = "/{version}/users", version = "2.0")
    public List<UserDTOv2> getUsersV2() { ... }
}
```

The `version = "1.0"` attribute in `@GetMapping` is new in Spring Framework 7: Spring automatically resolves which handler to invoke based on the detected version.

---

## Configuration

`WebConfig.java` centralizes all versioning configuration. **The most important rule:** the strategy chosen here must be consistent with the `value` in all controller `@GetMapping`s. Strategies cannot be mixed for the same resource.

---

### Option A — Path Segment (`usePathSegment`)

The version is embedded in the URL path.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .addSupportedVersions("1.0", "1.1", "2.0")
                .setDefaultVersion("1.0")
                .usePathSegment(1);  // /api/{version}/users
    }
}
```

Controllers must use `/{version}` in the path:

```java
@GetMapping(value = "/{version}/users", version = "1.0")
@GetMapping(value = "/{version}/users", version = "1.1")
@GetMapping(value = "/{version}/users", version = "2.0")
```

Requests:
```bash
http :8080/api/v1/users
http :8080/api/v1.1/users
http :8080/api/v2/users
```

---

### Option B — Request Header (`useRequestHeader`)

The version goes in a custom HTTP header. The path is always the same.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .addSupportedVersions("1.0", "1.1", "2.0")
                .setDefaultVersion("1.0")
                .setVersionParser(new ApiVersionParser()) // normalizes "v1" → "1.0"
                .useRequestHeader("X-API-Version");
    }
}
```

Controllers must use the same path without `/{version}`:

```java
@GetMapping(value = "/users", version = "1.0")
@GetMapping(value = "/users", version = "1.1")
@GetMapping(value = "/users", version = "2.0")
```

Requests:
```bash
http :8080/header/users X-API-Version:1.0
http :8080/header/users X-API-Version:1.1
http :8080/header/users X-API-Version:2.0
```

---

### Why strategies cannot be mixed

If `WebConfig` configures `useRequestHeader` but the controller leaves `/{version}/users` in some mappings and `/users` in others, Spring cannot resolve the correct handler and returns **404**. All handlers for the same resource must have the same path pattern.

| WebConfig setting          | Path in @GetMapping      | Result        |
|----------------------------|--------------------------|---------------|
| `usePathSegment(1)`        | `/{version}/users`       | Works         |
| `useRequestHeader(...)`    | `/users`                 | Works         |
| `useRequestHeader(...)`    | `/{version}/users`       | 404           |
| `usePathSegment(1)`        | `/users`                 | 404           |

---

### ApiVersionParser (custom)

Normalizes the version format sent by the client:

```java
public class ApiVersionParser implements org.springframework.web.accept.ApiVersionParser {

    @Override
    public Comparable parseVersion(String version) {
        // Accepts "v1", "V2", "1", "1.0" → normalizes to "1.0", "2.0"
        if (version.startsWith("v") || version.startsWith("V")) {
            version = version.substring(1);
        }
        if (!version.contains(".")) {
            version = version + ".0";
        }
        return version;
    }
}
```

With this, the client can send `X-API-Version: v1` or `X-API-Version: 1` and both are correctly resolved to `1.0`.

---

### Multiple controllers, multiple strategies

To **demonstrate both strategies** in the same project, the correct approach is to have separate controllers with different base paths:

```
/api/users          → path segment versioning  (UserVersionController)
/header/users       → header versioning         (UserHeaderController)
```

However, `WebConfig` can only have **one active configuration** at a time. Supporting both simultaneously would require two separate MVC contexts, which is advanced and impractical in production.

---

## Endpoints

This project has **two controllers** demonstrating both strategies. Only one can be active in `WebConfig` at a time.

---

### Strategy A — Path Segment (`UserVersionController`)

Activate in `WebConfig`: `.usePathSegment(1)`

| Version | Request |
|---------|---------|
| 1.0 | `http :8080/api/v1/users` |
| 1.1 | `http :8080/api/v1.1/users` |
| 2.0 | `http :8080/api/v2/users` |

**Response v1.0:**
```json
[{ "id": 1, "name": "Dan Vega", "email": "danvega@gmail.com", "webSite": "https://www.danvega.com" }]
```

**Response v2.0:**
```json
[{ "id": 1, "name": "Dan", "email": "danvega@gmail.com", "lastName": "Vega" }]
```

---

### Strategy B — Request Header (`UserHeaderController`)

Activate in `WebConfig`: `.useRequestHeader("X-API-Version")`

| Version | Request |
|---------|---------|
| 1.0 | `http :8080/header/users X-API-Version:1.0` |
| 1.1 | `http :8080/header/users X-API-Version:1.1` |
| 2.0 | `http :8080/header/users X-API-Version:2.0` |

`ApiVersionParser` also accepts the `v` prefix:
```bash
http :8080/header/users X-API-Version:v2
# normalizes "v2" → "2.0" and returns UserDTOv2
```

---

## Testing

### Strategy A — Integration tests (`UserApiVersionTest`)

Uses `@SpringBootTest(webEnvironment = RANDOM_PORT)` with `RestTestClient` configured with `ApiVersionInserter.usePathSegment(1)`. Requires `WebConfig` to use `usePathSegment`.

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserApiVersionTest {

    @BeforeEach
    void setUp() {
        restTestClient = RestTestClient
                .bindToServer()
                .baseUrl("http://localhost:" + port)
                .apiVersionInserter(ApiVersionInserter.usePathSegment(1))
                .build();
    }

    @Test
    void givenUser_whenGetUsers_thenReturnV1() {
        restTestClient.get().uri("/api/users").apiVersion("1.0")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].webSite").isEqualTo("https://www.danvega.com")
                .jsonPath("$[0].firstName").doesNotExist();
    }

    @Test
    void givenUnsupportedVersion_whenGetUsers_thenReturnBadRequest() {
        restTestClient.get().uri("/api/users").apiVersion("1.2")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
```

### Strategy B — Unit tests (`UserHeaderControllerTest`)

Uses `@WebMvcTest(UserHeaderController.class)` with a nested `@TestConfiguration` that activates header versioning. The main `WebConfig` is excluded to avoid conflicts.

```java
@WebMvcTest(
    controllers = UserHeaderController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)
)
@Import(UserHeaderControllerTest.HeaderVersioningConfig.class)
public class UserHeaderControllerTest {

    @TestConfiguration
    static class HeaderVersioningConfig implements WebMvcConfigurer {
        @Override
        public void configureApiVersioning(ApiVersionConfigurer configurer) {
            configurer
                .addSupportedVersions("1.0", "1.1", "2.0")
                .setDefaultVersion("1.0")
                .setVersionParser(new ApiVersionParser())
                .useRequestHeader("X-API-Version");
        }
    }

    @Test
    void givenUser_whenGetUsersV2_thenReturnUserDTOv2() throws Exception {
        // mock setup ...
        mockMvc.perform(get("/header/users").header("X-API-Version", "2.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lastName").value("Vega"))
                .andExpect(jsonPath("$[0].webSite").doesNotExist());
    }

    @Test
    void givenUnsupportedVersion_whenGetUsers_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/header/users").header("X-API-Version", "9.9"))
                .andExpect(status().isBadRequest());
    }
}
```

> **Note:** Spring Boot 4 replaces Mockito's `@MockBean` with `@MockitoBean` as a native annotation.

---

## How to Run

```bash
# Clone the repository
git clone <repo-url>
cd version-api

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

The application starts at `http://localhost:8080`.

---

## References

- [API Versioning in Spring — spring.io blog (Sep 2025)](https://spring.io/blog/2025/09/16/api-versioning-in-spring)
- [Spring Boot Built-in API Versioning — Piotr Minkowski (Dec 2025)](https://piotrminkowski.com/2025/12/01/spring-boot-built-in-api-versioning/)
- [Dan Vega — Spring Developer Advocate](https://www.danvega.dev/)
- [Spring Framework 7 Release Notes](https://spring.io/projects/spring-framework)
