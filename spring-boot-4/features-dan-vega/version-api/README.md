********# API Versioning en Spring Boot 4

> Proyecto de aprendizaje basado en las nuevas capacidades nativas de versionamiento de API introducidas en **Spring Framework 7** y **Spring Boot 4**, inspirado en el trabajo de [Dan Vega](https://www.danvega.dev/).

---

## Tabla de Contenidos

- [Contexto](#contexto)
- [Stack Tecnologico](#stack-tecnologico)
- [Que hay de nuevo en Spring Framework 7](#que-hay-de-nuevo-en-spring-framework-7)
- [Estrategias de Versionamiento](#estrategias-de-versionamiento)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Como funciona este proyecto](#como-funciona-este-proyecto)
- [Configuracion](#configuracion)
- [Endpoints](#endpoints)
- [Testing](#testing)
- [Como ejecutar](#como-ejecutar)
- [Referencias](#referencias)

---

## Contexto

Antes de Spring Framework 7, versionar una API REST en Spring requeria soluciones manuales o librerias de terceros:

- Definir manualmente paths con `/v1/`, `/v2/`
- Usar condiciones custom en `@RequestMapping`
- Delegar el routing a un API Gateway externo

**Spring Framework 7** introduce soporte nativo de versionamiento directamente en el MVC, eliminando ese boilerplate y ofreciendo una estrategia declarativa y configurable.

---

## Stack Tecnologico

| Tecnologia     | Version |
|----------------|---------|
| Java           | 25      |
| Spring Boot    | 4.0.5   |
| Spring Framework | 7.x   |
| Spring MVC     | Built-in API Versioning |
| Maven          | 3.x     |

---

## Que hay de nuevo en Spring Framework 7

Spring Framework 7 agrega soporte nativo de API versioning en Spring MVC a traves de:

### `ApiVersionConfigurer` (nuevo en Spring MVC)

Se configura via `WebMvcConfigurer.configureApiVersioning()`, permitiendo definir:

- Las versiones soportadas: `addSupportedVersions("1.0.0", "2.0.0")`
- La version por defecto: `setDefaultVersion("1.0.0")`
- La estrategia de resolucion de version

### Estrategias de resolucion de version

| Estrategia         | Metodo de configuracion       | Ejemplo                          |
|--------------------|-------------------------------|----------------------------------|
| Path segment       | `usePathSegment(int pos)`     | `GET /api/v1/users`              |
| Request header     | `useRequestHeader(String)`    | `X-API-Version: 1.0.0`           |
| Query parameter    | `useQueryParam(String)`       | `GET /api/users?version=1.0.0`   |
| Media type param   | `useMediaTypeParam(String)`   | `Accept: application/json;version=1` |

### Anotacion `version` en mappings

El atributo `version` se puede usar directamente en `@GetMapping`, `@PostMapping`, etc.:

```java
@GetMapping(value = "/{version}/users", version = "1.0.0")
public List<UserDTOv1> getUsers() { ... }

@GetMapping(value = "/{version}/users", version = "2.0.0")
public List<UserDTOv2> getUsersV2() { ... }
```

Spring resuelve el handler correcto segun la version detectada en la request.

---

## Estrategias de Versionamiento

### 1. Path Segment (usado en este proyecto)

La version va embebida en el path de la URL como un segmento:

```
GET /api/v1/users
GET /api/v2/users
```

**Ventajas:** Simple, visible, cacheable, facil de probar con curl/Postman.
**Desventajas:** Rompe el principio REST estricto (la URI deberia identificar el recurso, no su version).

### 2. Request Header

```
GET /api/users
X-API-Version: 2.0.0
```

**Ventajas:** URI limpia.
**Desventajas:** Menos visible, requiere configuracion del cliente.

### 3. Query Parameter

```
GET /api/users?version=2.0.0
```

**Ventajas:** Simple de probar en el browser.
**Desventajas:** Poco convencional para REST.

### 4. Media Type Parameter (Content Negotiation)

```
Accept: application/json;version=2
```

**Ventajas:** Sigue el estandar HTTP de negociacion de contenido.
**Desventajas:** Mas complejo de configurar y documentar.

---

## Estructura del Proyecto

```
src/main/java/com/spring4/version/
├── VersionApiApplication.java      # Entry point
├── User.java                       # Entidad de dominio (record)
├── UserRepository.java             # Repositorio en memoria
├── UserMapper.java                 # Mapeador User -> DTOs
├── UserDTOv1.java                  # DTO version 1.0 (id, name, email)
├── UserDTOv2.java                  # DTO version 2.0 (id, name, email, phoneNumber)
├── UserVersionController.java      # Strategy A: Path Segment (/api/{version}/users)
├── UserHeaderController.java       # Strategy B: Request Header (/header/users)
└── config/
    ├── WebConfig.java              # Configuracion activa de API versioning
    └── ApiVersionParser.java       # Normaliza "v1", "1" -> "1.0"
```

---

## Como funciona este proyecto

### Dominio

`User` es un Java Record con validacion en el compact constructor:

```java
public record User(Integer id, String name, String email) {
    public User {
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank");
        }
    }
}
```

### DTOs por version

**v1** — campos basicos:
```java
public record UserDTOv1(Integer id, String name, String email) {}
```

**v2** — agrega `phoneNumber` (mapeado desde el apellido del nombre como demo):
```java
public record UserDTOv2(Integer id, String name, String email, String phoneNumber) {}
```

### Mapper

`UserMapper` convierte el dominio a cada version de DTO:

```java
@Component
public class UserMapper {

    public UserDTOv1 toV1(User user) {
        return new UserDTOv1(user.id(), user.name(), user.email());
    }

    public UserDTOv2 toV2(User user) {
        String[] nameParts = user.name().split(" ");
        return new UserDTOv2(user.id(), nameParts[0], user.email(),
                nameParts.length > 1 ? nameParts[1] : "");
    }
}
```

### Controller

```java
@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping(value = "/{version}/users", version = "1.0.0")
    public List<UserDTOv1> getUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toV1)
                .toList();
    }
}
```

El atributo `version = "1.0.0"` en `@GetMapping` es la novedad de Spring Framework 7: Spring resuelve automaticamente cual handler invocar segun la version detectada.

---

## Configuracion

`WebConfig.java` centraliza toda la configuracion de versioning. **La regla mas importante:** la estrategia elegida aqui debe ser consistente con el `value` de todos los `@GetMapping` del controller. No se pueden mezclar estrategias en el mismo recurso.

---

### Opcion A — Path Segment (`usePathSegment`)

La version va embebida en el path de la URL.

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

Los controllers deben usar `/{version}` en el path:

```java
@GetMapping(value = "/{version}/users", version = "1.0")
@GetMapping(value = "/{version}/users", version = "1.1")
@GetMapping(value = "/{version}/users", version = "2.0")
```

Llamadas:
```bash
http :8080/api/v1/users
http :8080/api/v1.1/users
http :8080/api/v2/users
```

---

### Opcion B — Request Header (`useRequestHeader`)

La version va en un header HTTP personalizado. El path es siempre el mismo.

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureApiVersioning(ApiVersionConfigurer configurer) {
        configurer
                .addSupportedVersions("1.0", "1.1", "2.0")
                .setDefaultVersion("1.0")
                .useRequestHeader("X-API-Version")
                .setVersionParser(new ApiVersionParser()); // normaliza "v1" -> "1.0"
    }
}
```

Los controllers deben usar el mismo path sin `/{version}`:

```java
@GetMapping(value = "/users", version = "1.0")
@GetMapping(value = "/users", version = "1.1")
@GetMapping(value = "/users", version = "2.0")
```

Llamadas:
```bash
http :8080/api/users X-API-Version:1.0
http :8080/api/users X-API-Version:1.1
http :8080/api/users X-API-Version:2.0
```

---

### Por que NO se pueden mezclar

Si en `WebConfig` configuras `useRequestHeader` pero en el controller dejas `/{version}/users` en algunos mappings y `/users` en otros, Spring no puede resolver el handler correcto y retorna **404**. Todos los handlers del mismo recurso deben tener el mismo path pattern.

| Configuracion en WebConfig | Path en @GetMapping      | Resultado     |
|----------------------------|--------------------------|---------------|
| `usePathSegment(1)`        | `/{version}/users`       | Funciona      |
| `useRequestHeader(...)`    | `/users`                 | Funciona      |
| `useRequestHeader(...)`    | `/{version}/users`       | 404           |
| `usePathSegment(1)`        | `/users`                 | 404           |

---

### ApiVersionParser (custom)

Permite normalizar el formato de version que envia el cliente:

```java
public class ApiVersionParser implements org.springframework.web.accept.ApiVersionParser {

    @Override
    public Comparable parseVersion(String version) {
        // Acepta "v1", "V2", "1", "1.0" -> normaliza a "1.0", "2.0"
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

Con esto el cliente puede enviar `X-API-Version: v1` o `X-API-Version: 1` y ambos se resuelven correctamente a `1.0`.

---

### Multiples controllers, multiples estrategias

Si quieres **demostrar ambas estrategias** en el mismo proyecto, la forma correcta es tener controllers separados con base paths distintos:

```
/api/users          → header versioning  (UserHeaderController)
/api/path/users     → path versioning    (UserPathController)
```

Pero `WebConfig` solo puede tener **una configuracion activa** a la vez. Para soportar ambas simultaneamente necesitarias dos contextos MVC separados, lo cual es avanzado y poco practico en produccion.

---

## Endpoints

Este proyecto tiene **dos controllers** que demuestran las dos estrategias. Solo una puede estar activa en `WebConfig` a la vez.

---

### Strategy A — Path Segment (`UserVersionController`)

Activar en `WebConfig`: `.usePathSegment(1)`

| Version | Request |
|---------|---------|
| 1.0 | `http :8080/api/v1/users` |
| 1.1 | `http :8080/api/v1.1/users` |
| 2.0 | `http :8080/api/v2/users` |

**Response v1.0:**
```json
[{ "id": 1, "name": "Dan Vega", "email": "danvega@gmail.com" }]
```

**Response v2.0:**
```json
[{ "id": 1, "name": "Dan", "email": "danvega@gmail.com", "phoneNumber": "Vega" }]
```

---

### Strategy B — Request Header (`UserHeaderController`)

Activar en `WebConfig`: `.useRequestHeader("X-API-Version")`

| Version | Request |
|---------|---------|
| 1.0 | `http :8080/header/users X-API-Version:1.0` |
| 1.1 | `http :8080/header/users X-API-Version:1.1` |
| 2.0 | `http :8080/header/users X-API-Version:2.0` |

El `ApiVersionParser` tambien acepta formato con prefijo `v`:
```bash
http :8080/header/users X-API-Version:v2
# normaliza "v2" -> "2.0" y retorna UserDTOv2
```

---

## Testing

Spring Boot 4 incluye `spring-boot-starter-webmvc-test` como dependencia de test dedicada para MVC.

### Estrategia de testing para API versionada

Para testear correctamente endpoints versionados hay que cubrir estos escenarios:

#### 1. Test de version correcta — retorna datos esperados

```java
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    UserRepository userRepository;

    @MockitoBean
    UserMapper userMapper;

    @Test
    void shouldReturnUsersV1() throws Exception {
        var user = new User(1, "Dan Vega", "danvega@gmail.com");
        var dto = new UserDTOv1(1, "Dan Vega", "danvega@gmail.com");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toV1(user)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dan Vega"))
                .andExpect(jsonPath("$[0].email").value("danvega@gmail.com"));
    }
}
```

> **Nota:** Spring Boot 4 reemplaza `@MockBean` de Mockito por `@MockitoBean` como anotacion nativa.

#### 2. Test de version no soportada — debe retornar 400 o 404

```java
@Test
void shouldReturn4xxForUnsupportedVersion() throws Exception {
    mockMvc.perform(get("/api/v99/users"))
            .andExpect(status().is4xxClientError());
}
```

#### 3. Test de version por defecto

Cuando no se especifica version, debe comportarse como `1.0.0`:

```java
@Test
void shouldUseDefaultVersionWhenNotSpecified() throws Exception {
    // Depende de la estrategia configurada (header, query param, etc.)
    mockMvc.perform(get("/api/users")
                    .header("X-API-Version", "1.0.0"))
            .andExpect(status().isOk());
}
```

#### 4. Test de integracion con `@SpringBootTest`

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnUsersOnV1Path() {
        var response = restTemplate.getForEntity("/api/v1/users", UserDTOv1[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }
}
```

### Que agregar al proyecto (sugerencias)

| Que agregar                        | Por que                                                          |
|------------------------------------|------------------------------------------------------------------|
| Endpoint v2 en `UserController`    | Demostrar routing entre versiones en el mismo controller         |
| `@ControllerAdvice` para errores   | Retornar respuestas claras cuando la version no es soportada     |
| Tests con `@WebMvcTest`            | Validar comportamiento por version de forma aislada              |
| Tests de integracion               | Verificar que el `WebConfig` aplica el versionamiento correcto   |
| Documentacion con SpringDoc/OpenAPI| Exponer cada version en la UI de Swagger                         |

---

## Como ejecutar

```bash
# Clonar el repositorio
git clone <repo-url>
cd version-api

# Ejecutar
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test
```

La aplicacion levanta en `http://localhost:8080`.

---

## Referencias

- [API Versioning in Spring — spring.io blog (Sep 2025)](https://spring.io/blog/2025/09/16/api-versioning-in-spring)
- [Spring Boot Built-in API Versioning — Piotr Minkowski (Dec 2025)](https://piotrminkowski.com/2025/12/01/spring-boot-built-in-api-versioning/)
- [Dan Vega — Spring Developer Advocate](https://www.danvega.dev/)
- [Spring Framework 7 Release Notes](https://spring.io/projects/spring-framework)