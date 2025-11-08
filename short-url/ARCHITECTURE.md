# 🏗️ Arquitectura Detallada: Spring Modulith

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Módulos y Responsabilidades](#módulos-y-responsabilidades)
3. [Capa Web (Thymeleaf)](#capa-web-thymeleaf)
4. [Flujo de Datos](#flujo-de-datos)
5. [Comunicación entre Módulos](#comunicación-entre-módulos)
6. [Guía de Refactorización](#guía-de-refactorización)

---

## 🎯 Visión General

```
┌─────────────────────────────────────────────────────────────────┐
│                     CLIENTE (Navegador)                         │
├─────────────────────────────────────────────────────────────────┤
│ HTTP Request                                                    │
└────────────────┬────────────────────────────────────────────────┘
                 │
                 ↓
┌─────────────────────────────────────────────────────────────────┐
│                  🌐 WEB MODULE (Presentación)                   │
│  • Controllers: @GetMapping, @PostMapping                       │
│  • Templates: Thymeleaf HTML                                    │
│  • Forms: Validación de entrada                                 │
│  • Static: CSS, JavaScript, Imágenes                            │
└────────────┬──────────────────────────┬──────────────┬──────────┘
             │                          │              │
             ↓                          ↓              ↓
    ┌────────────────┐      ┌──────────────────┐  ┌───────────────┐
    │  📦 shorturl/  │      │   📦 user/       │  │ 📦 notification/│
    │  internal/     │      │  internal/       │  │ internal/      │
    │  Services      │      │  Services        │  │ Services       │
    └────────┬───────┘      └────────┬─────────┘  └───────────────┘
             │                       │
             ↓                       ↓
    ┌────────────────┐      ┌──────────────────┐
    │  Repository    │      │  Repository      │
    │  ShortUrl      │      │  User            │
    │  (JPA)         │      │  (JPA)           │
    └────────┬───────┘      └────────┬─────────┘
             │                       │
             ↓                       ↓
    ┌──────────────────────────────────────────┐
    │      📊 PostgreSQL Database              │
    └──────────────────────────────────────────┘
```

---

## 📦 Módulos y Responsabilidades

### 1️⃣ **Módulo `shorturl`** - Acortamiento de URLs

**Ubicación**: `src/main/java/com/manuonda/urlshortener/shorturl/`

```
shorturl/
├── internal/                           ← PRIVADO (no accesible desde otros módulos)
│   ├── ShortUrlService.java
│   │   - Crear URLs acortadas
│   │   - Validar URLs existentes
│   │   - Registrar clicks
│   │   - Publicar ShortUrlCreatedEvent
│   │
│   ├── ShortUrlRepository.java         ← JPA Repository
│   │   - Queries personalizadas
│   │   - Persistencia en BD
│   │
│   ├── UrlExistenceValidator.java      ← Utilidad privada
│   │   - Validar que URL existe (HTTP)
│   │
│   └── EntityMapper.java               ← Mapeo Entidad→DTO
│       - Convertir ShortUrl → ShortUrlDto
│
├── api/                                ← PÚBLICO (accesible desde otros módulos)
│   ├── ShortUrlDto.java
│   │   public record ShortUrlDto(
│   │       String shortKey,
│   │       String originalUrl,
│   │       Long clickCount,
│   │       Instant createdAt
│   │   ) {}
│   │
│   └── CreateShortUrlCmd.java
│       public record CreateShortUrlCmd(
│           String originalUrl,
│           Boolean isPrivate,
│           Long expirationInDays,
│           Long userId
│       ) {}
│
└── ShortUrlCreatedEvent.java          ← Evento público
    public record ShortUrlCreatedEvent(
        String shortKey,
        String originalUrl,
        Long userId,
        Instant createdAt
    ) implements DomainEvent {}
```

**Acceso desde otros módulos**:
```java
// ✅ PERMITIDO (público)
import com.manuonda.urlshortener.shorturl.ShortUrlDto;
import com.manuonda.urlshortener.shorturl.CreateShortUrlCmd;
import com.manuonda.urlshortener.shorturl.ShortUrlCreatedEvent;

// ❌ NO PERMITIDO (privado)
import com.manuonda.urlshortener.shorturl.internal.ShortUrlService;
```

---

### 2️⃣ **Módulo `user`** - Gestión de Usuarios

**Ubicación**: `src/main/java/com/manuonda/urlshortener/user/`

```
user/
├── internal/                           ← PRIVADO
│   ├── UserService.java
│   │   - Crear usuarios
│   │   - Validar credenciales
│   │   - Publicar UserCreatedEvent
│   │
│   ├── UserRepository.java             ← JPA Repository
│   │   - Queries de usuarios
│   │   - Persistencia
│   │
│   └── PasswordEncoder.java            ← Utilidad privada
│       - Hash de contraseñas
│
├── api/                                ← PÚBLICO
│   ├── UserDto.java
│   │   public record UserDto(
│   │       Long id,
│   │       String username,
│   │       String email,
│   │       Instant createdAt
│   │   ) {}
│   │
│   └── CreateUserCmd.java
│       public record CreateUserCmd(
│           String username,
│           String email,
│           String password
│       ) {}
│
└── UserCreatedEvent.java              ← Evento público
    public record UserCreatedEvent(
        Long userId,
        String email,
        String username,
        Instant createdAt
    ) implements DomainEvent {}
```

---

### 3️⃣ **Módulo `notification`** - Notificaciones

**Ubicación**: `src/main/java/com/manuonda/urlshortener/notification/`

```
notification/
├── internal/                           ← PRIVADO
│   ├── NotificationService.java
│   │   @Service
│   │   public class NotificationService {
│   │       @ApplicationModuleListener
│   │       void onShortUrlCreated(ShortUrlCreatedEvent event) {
│   │           // Escucha eventos de shorturl
│   │           emailSender.send(...);
│   │       }
│   │
│   │       @ApplicationModuleListener
│   │       void onUserCreated(UserCreatedEvent event) {
│   │           // Escucha eventos de user
│   │           emailSender.send(...);
│   │       }
│   │   }
│   │
│   └── EmailSender.java
│       - Envía emails
│       - Puede usar JavaMailSender, SendGrid, etc.
│
└── (SIN api/ - No expone interfaces públicas)
```

**Características especiales**:
- No tiene paquete `api/` (no necesita interfaces públicas)
- Solo escucha eventos de otros módulos
- No puede ser inyectado directamente en otros módulos

---

### 4️⃣ **Módulo `web`** - Presentación (Controllers + Vistas)

**Ubicación**: `src/main/java/com/manuonda/urlshortener/web/` y `src/main/resources/templates/`

```
web/
├── controller/                         ← Controllers MVC
│   ├── HomeController.java
│   │   @Controller
│   │   @GetMapping("/")
│   │   public String home(Model model) {
│   │       // Accede a APIs públicas de otros módulos
│   │       model.addAttribute("shortUrls",
│   │           shortUrlService.findAllPublicShortUrls());
│   │       return "index";
│   │   }
│   │
│   ├── ShortUrlController.java
│   │   @Controller
│   │   @PostMapping("/short-urls")
│   │   public String createShortUrl(@Valid CreateShortUrlForm form) {
│   │       CreateShortUrlCmd cmd = new CreateShortUrlCmd(...);
│   │       shortUrlService.createShortUrl(cmd);
│   │       return "redirect:/";
│   │   }
│   │
│   └── UserController.java
│       @Controller
│       @PostMapping("/register")
│       public String register(@Valid RegisterUserRequest req) {
│           CreateUserCmd cmd = new CreateUserCmd(...);
│           userService.createUser(cmd);
│           return "redirect:/login";
│       }
│
├── dto/                                ← Form Objects
│   ├── CreateShortUrlForm.java
│   │   public record CreateShortUrlForm(
│   │       @NotBlank String originalUrl,
│   │       Boolean isPrivate,
│   │       Long expirationInDays
│   │   ) {}
│   │
│   └── RegisterUserRequest.java
│       public record RegisterUserRequest(
│           @NotBlank String username,
│           @Email String email,
│           @NotBlank String password
│       ) {}
│
└── templates/                          ← 🎨 VISTAS THYMELEAF
    ├── layout.html                     (Base layout compartida)
    │   <!DOCTYPE html>
    │   <html th:lang="es">
    │       <head>...</head>
    │       <body>
    │           <nav>...</nav>
    │           <th:block th:insert="~{::content}"></th:block>
    │       </body>
    │   </html>
    │
    ├── index.html                      (Home)
    │   <div th:each="url : ${shortUrls}">
    │       <a th:href="@{/s/{key}(key=${url.shortKey()})}">
    │           <span th:text="${url.shortKey()}"></span>
    │       </a>
    │   </div>
    │
    ├── urls/
    │   ├── list.html                   (Listar URLs del usuario)
    │   └── create.html                 (Formulario crear URL)
    │
    ├── users/
    │   ├── register.html               (Registro de usuarios)
    │   └── profile.html                (Perfil del usuario)
    │
    └── error/
        ├── 404.html                    (URL no encontrada)
        └── 500.html                    (Error interno)
```

**¿Quién accede a qué?**

```java
// En ShortUrlController.java (módulo web)

@Controller
public class ShortUrlController {

    // ✅ Inyecta SERVICIOS de otros módulos
    private final ShortUrlService shortUrlService;
    private final UserService userService;

    // ❌ NO puede inyectar clases internas
    // private final UrlExistenceValidator validator; // ❌ ERROR

    @PostMapping("/short-urls")
    public String create(@Valid CreateShortUrlForm form) {
        // ✅ Usa DTOs y Commands públicos
        CreateShortUrlCmd cmd = new CreateShortUrlCmd(
            form.originalUrl(),
            form.isPrivate(),
            form.expirationInDays(),
            null
        );

        // ✅ Accede a APIs públicas
        ShortUrlDto dto = shortUrlService.createShortUrl(cmd);

        // Renderiza vista Thymeleaf
        return "redirect:/";
    }
}
```

---

## 🌐 Capa Web (Thymeleaf)

### ¿Cómo funciona?

```
1. Usuario abre http://localhost:8080
                     ↓
2. Spring rutea a HomeController.home()
                     ↓
3. Controller llama a shortUrlService.findAllPublicShortUrls()
                     ↓
4. Service consulta BD y retorna List<ShortUrlDto>
                     ↓
5. Controller añade atributos al Model
   model.addAttribute("shortUrls", dtos);
                     ↓
6. Controller retorna nombre de vista: "index"
                     ↓
7. Thymeleaf renderiza templates/index.html
   - Lee atributos del Model
   - Genera HTML dinámico con th:each, th:if, etc.
                     ↓
8. HTML se envía al navegador
                     ↓
9. Navegador renderiza la página
```

### Ejemplo Completo: Crear URL Corta

**1. Vista HTML (templates/index.html)**
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>URL Shortener</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <div class="container">
        <!-- Mostrar URLs públicas -->
        <table class="table" th:if="${shortUrls != null}">
            <thead>
                <tr>
                    <th>Short Key</th>
                    <th>Original URL</th>
                    <th>Clicks</th>
                </tr>
            </thead>
            <tbody>
                <tr th:each="url : ${shortUrls}">
                    <td>
                        <a th:href="@{/s/{key}(key=${url.shortKey()})}"
                           th:text="${url.shortKey()}">
                        </a>
                    </td>
                    <td th:text="${url.originalUrl()}"></td>
                    <td th:text="${url.clickCount()}"></td>
                </tr>
            </tbody>
        </table>

        <!-- Formulario crear URL -->
        <form th:object="${createShortUrlForm}"
              method="post"
              action="/short-urls">

            <div class="form-group">
                <label for="originalUrl">Original URL:</label>
                <input type="url"
                       id="originalUrl"
                       th:field="*{originalUrl}"
                       class="form-control"
                       required>
                <span th:if="${#fields.hasErrors('originalUrl')}"
                      th:errors="*{originalUrl}"
                      class="error">
                </span>
            </div>

            <div class="form-group">
                <label for="isPrivate">
                    <input type="checkbox"
                           id="isPrivate"
                           th:field="*{isPrivate}">
                    Private URL
                </label>
            </div>

            <div class="form-group">
                <label for="expirationInDays">Expiration (days):</label>
                <input type="number"
                       id="expirationInDays"
                       th:field="*{expirationInDays}"
                       class="form-control"
                       min="1">
            </div>

            <button type="submit" class="btn btn-primary">
                Shorten URL
            </button>
        </form>
    </div>
</body>
</html>
```

**2. Controller (web/controller/ShortUrlController.java)**
```java
@Controller
public class ShortUrlController {

    private final ShortUrlService shortUrlService;
    private final ApplicationProperties properties;

    @GetMapping("/")
    public String home(Model model) {
        // 1. Llama servicio del módulo shorturl
        List<ShortUrlDto> urls = shortUrlService.findAllPublicShortUrls();

        // 2. Añade atributos al modelo para Thymeleaf
        model.addAttribute("shortUrls", urls);
        model.addAttribute("baseUrl", properties.baseUrl());
        model.addAttribute("createShortUrlForm",
            new CreateShortUrlForm("", false, 30));

        // 3. Retorna nombre de vista
        return "index";  // ← Renderiza templates/index.html
    }

    @PostMapping("/short-urls")
    public String createShortUrl(
            @ModelAttribute("createShortUrlForm") @Valid CreateShortUrlForm form,
            BindingResult result,
            Model model) {

        // 1. Validación de entrada
        if (result.hasErrors()) {
            model.addAttribute("shortUrls",
                shortUrlService.findAllPublicShortUrls());
            model.addAttribute("createShortUrlForm", form);
            return "index";  // Vuelve a mostrar formulario
        }

        try {
            // 2. Convierte Form → Command (DTO de dominio)
            CreateShortUrlCmd cmd = new CreateShortUrlCmd(
                form.originalUrl(),
                form.isPrivate(),
                form.expirationInDays(),
                null  // userId (null para usuarios anónimos)
            );

            // 3. Llama servicio para crear URL
            ShortUrlDto dto = shortUrlService.createShortUrl(cmd);

            // 4. Mensaje de éxito (flash attribute)
            redirectAttributes.addFlashAttribute("successMessage",
                "URL creada: " + properties.baseUrl() +
                "/s/" + dto.shortKey());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                "Error creando URL: " + e.getMessage());
        }

        // 5. Redirige a home (POST-Redirect-GET pattern)
        return "redirect:/";
    }

    @GetMapping("/s/{shortKey}")
    public String redirectToOriginalUrl(@PathVariable String shortKey) {
        // 1. Accede URL acortada
        Optional<ShortUrlDto> urlOpt =
            shortUrlService.accessShortUrl(shortKey);

        if (urlOpt.isEmpty()) {
            throw new ShortUrlNotFoundException(
                "URL no encontrada: " + shortKey);
        }

        // 2. Redirige a URL original
        return "redirect:" + urlOpt.get().originalUrl();
    }
}
```

**3. Service (shorturl/internal/ShortUrlService.java)**
```java
@Service
@Transactional(readOnly = true)
public class ShortUrlService {

    private final ShortUrlRepository repository;
    private final ApplicationEventPublisher events;
    private final EntityMapper mapper;

    @Transactional
    public ShortUrlDto createShortUrl(CreateShortUrlCmd cmd) {
        // 1. Crear entidad
        ShortUrl shortUrl = new ShortUrl();
        shortUrl.setShortKey(generateRandomShortKey());
        shortUrl.setOriginalUrl(cmd.originalUrl());
        shortUrl.setIsPrivate(cmd.isPrivate());
        shortUrl.setCreatedAt(Instant.now());

        // 2. Persistir
        repository.save(shortUrl);

        // 3. Publicar evento (módulo notification escuchará)
        events.publishEvent(new ShortUrlCreatedEvent(
            shortUrl.getShortKey(),
            shortUrl.getOriginalUrl(),
            cmd.userId(),
            shortUrl.getCreatedAt()
        ));

        // 4. Retornar DTO público
        return mapper.toShortUrlDto(shortUrl);
    }

    public List<ShortUrlDto> findAllPublicShortUrls() {
        return repository.findPublicShortUrl()
            .stream()
            .map(mapper::toShortUrlDto)
            .toList();
    }
}
```

---

## 🔄 Flujo de Datos

```
┌──────────────────────────────────────────────────────────┐
│ FLUJO: Usuario crea una URL corta                        │
└──────────────────────────────────────────────────────────┘

1. PRESENTACIÓN (web/controller)
   User submits form in index.html
   ↓
   ShortUrlController.createShortUrl(CreateShortUrlForm form)
   ↓

2. TRANSFORMACIÓN (web/dto → shorturl/api)
   CreateShortUrlForm → CreateShortUrlCmd
   ↓

3. LÓGICA DE NEGOCIO (shorturl/internal)
   ShortUrlService.createShortUrl(CreateShortUrlCmd cmd)
   ├─ Genera short key único
   ├─ Valida URL
   ├─ Crea entidad ShortUrl
   ↓

4. PERSISTENCIA
   ShortUrlRepository.save(shortUrl)
   ├─ INSERT en tabla short_urls
   ↓

5. EVENTOS (Domain-Driven Design)
   events.publishEvent(ShortUrlCreatedEvent)
   ↓

6. OTRAS MÓDULOS (Escucha de eventos)
   notification/NotificationService
   @ApplicationModuleListener
   ├─ onShortUrlCreated(ShortUrlCreatedEvent)
   ├─ emailSender.send(...)
   ↓

7. RESPUESTA
   ShortUrlService retorna ShortUrlDto
   ↓
   Controller redirige a home
   ↓
   Thymeleaf renderiza lista actualizada
   ↓
   Navegador muestra página con nueva URL
```

---

## 🔐 Límites de Módulos (Spring Modulith Enforces)

```
┌─────────────────────────────────────────────────────┐
│                    web (presentación)              │
├─────────────────────────────────────────────────────┤
│ ✅ CAN ACCESS:                                      │
│   - shorturl.ShortUrlDto                            │
│   - shorturl.CreateShortUrlCmd                      │
│   - shorturl.ShortUrlCreatedEvent (para listeners) │
│   - user.UserDto                                    │
│   - user.CreateUserCmd                              │
│                                                     │
│ ❌ CANNOT ACCESS:                                   │
│   - shorturl.internal.ShortUrlService               │
│   - shorturl.internal.ShortUrlRepository            │
│   - user.internal.*                                 │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│             notification (eventos)                  │
├─────────────────────────────────────────────────────┤
│ ✅ CAN ACCESS:                                      │
│   - shorturl.ShortUrlCreatedEvent                   │
│   - user.UserCreatedEvent                          │
│   - notification.internal.* (su propio módulo)     │
│                                                     │
│ ❌ CANNOT ACCESS:                                   │
│   - shorturl.internal.*                             │
│   - user.internal.*                                 │
│   - web.* (presentación)                            │
└─────────────────────────────────────────────────────┘
```

---

## 📋 Guía de Refactorización

### Paso 1: Crear Estructura de Directorios

```bash
# Desde la raíz del proyecto
mkdir -p src/main/java/com/manuonda/urlshortener/{shorturl,user,notification,web}/{internal,api}

# shorturl
mkdir -p src/main/java/com/manuonda/urlshortener/shorturl/internal
mkdir -p src/main/java/com/manuonda/urlshortener/shorturl/api

# user
mkdir -p src/main/java/com/manuonda/urlshortener/user/internal
mkdir -p src/main/java/com/manuonda/urlshortener/user/api

# notification
mkdir -p src/main/java/com/manuonda/urlshortener/notification/internal

# web
mkdir -p src/main/java/com/manuonda/urlshortener/web/controller
mkdir -p src/main/java/com/manuonda/urlshortener/web/dto
```

### Paso 2: Mover Archivos

**shorturl/api/**
```
ShortUrlDto.java              ← Mover desde domain/models/
CreateShortUrlCmd.java        ← Mover desde domain/models/
ShortUrlCreatedEvent.java     ← CREAR NUEVO
```

**shorturl/internal/**
```
ShortUrlService.java          ← Mover desde service/
ShortUrlRepository.java       ← Mover desde repositorys/
EntityMapper.java             ← Mover desde service/
UrlExistenceValidator.java    ← Mover desde service/
RandomUtils.java              ← Mover desde service/
```

### Paso 3: Actualizar Imports

Todos los imports deben apuntar a:
- `com.manuonda.urlshortener.shorturl.api.*` (DTOs)
- `com.manuonda.urlshortener.shorturl.internal.*` (desde web/controller)

### Paso 4: Agregar Spring Modulith

**pom.xml**
```xml
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-starter-core</artifactId>
    <version>1.3.0</version>
</dependency>
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-testing</artifactId>
    <version>1.3.0</version>
    <scope>test</scope>
</dependency>
```

### Paso 5: Test de Arquitectura

```java
// src/test/java/com/manuonda/urlshortener/ModuleStructureTest.java

@SpringBootTest
class ModuleStructureTest {

    @Test
    void testModuleStructure() {
        ApplicationModules modules =
            ApplicationModules.of(SpringBootUrlShortenerApplication.class);
        modules.verify();
    }
}
```

---

## 📚 Resumen Visual

```
┌─────────────────────────────────────────────────────────────┐
│                 🌐 WEB (Presentación)                       │
│  Controllers + Thymeleaf Templates + Static Files           │
└──────────┬─────────────┬──────────────┬─────────────────────┘
           │             │              │
      Inyecta       Inyecta        Inyecta
           │             │              │
    ┌──────▼────┐  ┌─────▼───┐  ┌──────▼──────┐
    │ shorturl   │  │  user   │  │ notification│
    │ Service    │  │ Service │  │ Service     │
    └──────┬─────┘  └─────┬───┘  └─────────────┘
           │              │      Escucha eventos
           ↓              ↓
    ┌──────────────────────────┐
    │   📊 PostgreSQL Database │
    └──────────────────────────┘
```

---

**Conclusión**: El módulo `web` es responsable de:
- 🎨 Renderizar vistas Thymeleaf
- 🔄 Recibir datos del navegador
- 📤 Enviar datos a los servicios de otros módulos
- 📥 Recibir respuestas de otros módulos
- 🔄 Actualizar vistas con nuevos datos