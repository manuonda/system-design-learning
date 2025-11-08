# 🎯 Spring Modulith: Guía Rápida para tu Proyecto

## ¿Quién maneja QUÉ?

### 🌐 **Módulo `web/`** - TODA la presentación con Thymeleaf

```
┌────────────────────────────────────────────────────────┐
│                  NAVEGADOR (HTML)                      │
└──────────────────────┬─────────────────────────────────┘
                       │ HTTP Request
                       ↓
┌────────────────────────────────────────────────────────┐
│          web/controller/ShortUrlController             │
│                                                        │
│  @GetMapping("/")                                      │
│  public String home(Model model) {                     │
│      // 1️⃣ Llama servicio                            │
│      List<URLs> = shortUrlService.getAll();           │
│                                                        │
│      // 2️⃣ Prepara datos para vista                   │
│      model.addAttribute("shortUrls", URLs);           │
│                                                        │
│      // 3️⃣ Retorna nombre de vista                    │
│      return "index";  ← 🎨 Renderiza Thymeleaf       │
│  }                                                     │
└────────────┬──────────────────────┬────────────────────┘
             │                      │
             │                      │
      Inyecta      Retorna
       Service      DTO
             │                      │
             ↓                      ↓
   ┌─────────────────┐     ┌──────────────────┐
   │ shorturl/       │     │ templates/       │
   │ internal/       │     │ index.html       │
   │ ShortUrlService │     │ (Thymeleaf)      │
   └────────┬────────┘     └────────┬─────────┘
            │                       │
            │ Crea/Lee BD           │
            ↓                       │
   ┌─────────────────┐              │
   │ Repository      │              │
   │ PostgreSQL      │              │
   └─────────────────┘              │
                                    │ Renderiza
                                    ↓
                        ┌──────────────────────┐
                        │   HTML + CSS/JS      │
                        │ (rendered at server) │
                        └──────────────────────┘
                                    │
                                    │ HTTP Response
                                    ↓
                        ┌──────────────────────┐
                        │  Navegador Renderiza │
                        │   Página HTML        │
                        └──────────────────────┘
```

---

## 📊 Tabla: Qué hace cada módulo

```
┌──────────────────────────────────────────────────────────────────┐
│                          MÓDULOS                                 │
├────────┬─────────────────┬──────────────┬────────────────────────┤
│ Módulo │ Responsabilidad │ Tiene        │ Características        │
│        │                 │ Controllers? │                        │
├────────┼─────────────────┼──────────────┼────────────────────────┤
│ web    │ Presentación    │ ✅ SÍ        │ • Controllers MVC      │
│        │ (UI)            │              │ • Templates Thymeleaf  │
│        │                 │              │ • Manejo de formularios│
│        │                 │              │ • Static files (CSS/JS)│
├────────┼─────────────────┼──────────────┼────────────────────────┤
│shorturl│ Acortamiento    │ ❌ NO        │ • Lógica de URLs       │
│        │ de URLs         │              │ • Validación           │
│        │                 │              │ • Persistencia         │
│        │                 │              │ • Publica eventos      │
├────────┼─────────────────┼──────────────┼────────────────────────┤
│ user   │ Gestión de      │ ❌ NO        │ • CRUD de usuarios     │
│        │ usuarios        │              │ • Autenticación        │
│        │                 │              │ • Publica eventos      │
├────────┼─────────────────┼──────────────┼────────────────────────┤
│notif.  │ Notificaciones  │ ❌ NO        │ • Escucha eventos      │
│        │ (reacción)      │              │ • Envía emails/SMS     │
│        │                 │              │ • Reaccionario (passivo)│
└────────┴─────────────────┴──────────────┴────────────────────────┘
```

---

## 🔀 Flujo de Ejemplo: Crear URL Corta

```
┌─────────────────────────────────────────────────────────────────┐
│ PASO 1: USUARIO en Navegador                                   │
└─────────────────────────────────────────────────────────────────┘

Usuario abre: http://localhost:8080
      ↓
HomeController.home()
      ↓
Retorna: "index"
      ↓
Thymeleaf renderiza: templates/index.html
      ↓
Navegador muestra formulario + lista de URLs

┌─────────────────────────────────────────────────────────────────┐
│ PASO 2: USUARIO LLENA FORMULARIO Y ENVÍA                       │
└─────────────────────────────────────────────────────────────────┘

Usuario: https://www.ejemplo.com/articulo-muy-largo-xxx
      ↓
Form submit POST a /short-urls
      ↓
ShortUrlController.createShortUrl(CreateShortUrlForm form)
      ↓
Convierte:  CreateShortUrlForm (del formulario)
         ↓
         CreateShortUrlCmd (DTO de dominio)
      ↓
shortUrlService.createShortUrl(cmd)

┌─────────────────────────────────────────────────────────────────┐
│ PASO 3: LÓGICA EN SERVICIO (shorturl module)                   │
└─────────────────────────────────────────────────────────────────┘

ShortUrlService.createShortUrl(cmd)
      ↓
1️⃣ Genera short key: "abc123"
      ↓
2️⃣ Valida URL (opcional)
      ↓
3️⃣ Crea entidad ShortUrl
      ↓
4️⃣ Persiste en BD:
   INSERT INTO short_urls (short_key, original_url, ...)
      ↓
5️⃣ 🔔 PUBLICA EVENTO:
   ShortUrlCreatedEvent {
       shortKey: "abc123",
       originalUrl: "https://...",
       userId: null,
       createdAt: now()
   }
      ↓
6️⃣ Retorna:
   ShortUrlDto {
       shortKey: "abc123",
       originalUrl: "https://...",
       clickCount: 0,
       createdAt: now()
   }

┌─────────────────────────────────────────────────────────────────┐
│ PASO 4: OTROS MÓDULOS REACCIONAN (Escuchan eventos)            │
└─────────────────────────────────────────────────────────────────┘

NotificationService escucha: @ApplicationModuleListener
      ↓
@ApplicationModuleListener
void onShortUrlCreated(ShortUrlCreatedEvent event) {
    // Si hay usuario registrado, envía email
    if (event.userId() != null) {
        emailSender.send(
            "Tu URL fue acortada",
            "Nuevo short: " + event.shortKey()
        );
    }
}

┌─────────────────────────────────────────────────────────────────┐
│ PASO 5: RESPUESTA AL USUARIO (Web module)                      │
└─────────────────────────────────────────────────────────────────┘

ShortUrlController recibe ShortUrlDto
      ↓
Añade mensaje flash: "URL creada: abc123"
      ↓
Redirige: return "redirect:/";
      ↓
Navegador sigue redirección
      ↓
HomeController.home() se ejecuta de nuevo
      ↓
Obtiene lista actualizada de URLs (incluyendo la nueva)
      ↓
Thymeleaf renderiza templates/index.html
      ↓
Navegador muestra:
   ✅ Mensaje: "URL creada: http://localhost:8080/s/abc123"
   ✅ Nueva URL en la tabla

┌─────────────────────────────────────────────────────────────────┐
│ PASO 6: USUARIO ACCEDE A URL CORTA                             │
└─────────────────────────────────────────────────────────────────┘

Usuario hace clic en: http://localhost:8080/s/abc123
      ↓
ShortUrlController.redirectToOriginalUrl("abc123")
      ↓
shortUrlService.accessShortUrl("abc123")
      ↓
1️⃣ Busca en BD
2️⃣ Valida que no esté expirada
3️⃣ Incrementa click count
4️⃣ Retorna URL original
      ↓
Controller: return "redirect:" + originalUrl;
      ↓
Browser redirige a: https://www.ejemplo.com/articulo-muy-largo-xxx
      ↓
✅ Usuario ve el contenido original
```

---

## 🔐 Límites de Acceso (Spring Modulith Enforces Automatically)

### El módulo `web` PUEDE acceder a:

```java
✅ PERMITIDO:
├─ com.manuonda.urlshortener.shorturl.ShortUrlDto
├─ com.manuonda.urlshortener.shorturl.CreateShortUrlCmd
├─ com.manuonda.urlshortener.user.UserDto
├─ com.manuonda.urlshortener.user.CreateUserCmd
└─ (Cualquier clase en el paquete raíz de un módulo)

❌ NO PERMITIDO:
├─ com.manuonda.urlshortener.shorturl.internal.*
├─ com.manuonda.urlshortener.user.internal.*
├─ com.manuonda.urlshortener.shorturl.internal.UrlExistenceValidator
└─ (Nada en carpetas internas de otros módulos)
```

### Ejemplo de violación (que Spring Modulith detecta):

```java
// ❌ ESTO CAUSARÁ ERROR EN TEST

@Controller
public class ShortUrlController {

    // ❌ Error: No puedes inyectar servicios internos
    private final UrlExistenceValidator validator;

    // ✅ Correcto: Inyecta servicio que expone la API
    private final ShortUrlService service;
}
```

---

## 🎨 Thymeleaf: ¿Quién lo maneja?

### Solo el módulo `web`

```
┌─────────────────────────────────────────────────────────────────┐
│                    MÓDULO WEB                                  │
│                                                                 │
│  src/main/resources/templates/                                 │
│  ├── layout.html              ← Layout base                    │
│  ├── index.html               ← Home (lista URLs)              │
│  ├── urls/                                                     │
│  │   ├── create.html          ← Formulario crear URL           │
│  │   └── list.html            ← Historial usuario              │
│  ├── users/                                                    │
│  │   ├── register.html        ← Registro                       │
│  │   └── profile.html         ← Perfil usuario                 │
│  └── error/                                                    │
│      ├── 404.html             ← URL no encontrada              │
│      └── 500.html             ← Error servidor                 │
│                                                                 │
│  src/main/resources/static/                                    │
│  └── css/style.css            ← Estilos (opcional)             │
└─────────────────────────────────────────────────────────────────┘

Los módulos shorturl, user, notification NO tienen:
  ❌ Controllers
  ❌ Templates Thymeleaf
  ❌ Formularios
  ❌ Vistas HTML

Solo tienen:
  ✅ Servicios
  ✅ Repositorios
  ✅ Entidades
  ✅ Lógica de negocio
```

---

## 📝 Ejemplo: Template Thymeleaf

### templates/index.html

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title th:text="${title}">URL Shortener</title>
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <div class="container">
        <h1>URL Shortener</h1>

        <!-- 🎯 MENSAJE DE ÉXITO/ERROR (Flash Attributes) -->
        <div th:if="${successMessage}" class="alert alert-success">
            <span th:text="${successMessage}"></span>
        </div>
        <div th:if="${errorMessage}" class="alert alert-danger">
            <span th:text="${errorMessage}"></span>
        </div>

        <!-- 📋 TABLA DE URLs (th:each - iteración) -->
        <div th:if="${shortUrls != null and shortUrls.size() > 0}">
            <h2>URLs Disponibles</h2>
            <table class="table">
                <thead>
                    <tr>
                        <th>Short Key</th>
                        <th>Original URL</th>
                        <th>Clicks</th>
                        <th>Created</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- 🔄 TH:EACH - Itera sobre lista de modelo -->
                    <tr th:each="url : ${shortUrls}">
                        <!-- 🔗 Crear link dinámico -->
                        <td>
                            <a th:href="@{/s/{key}(key=${url.shortKey()})}"
                               target="_blank"
                               th:text="${url.shortKey()}">
                            </a>
                        </td>
                        <!-- 📄 Mostrar URL original -->
                        <td th:text="${url.originalUrl()}"></td>
                        <!-- 📊 Mostrar conteo de clicks -->
                        <td th:text="${url.clickCount()}"></td>
                        <!-- 📅 Fecha formateada -->
                        <td th:text="${#temporals.format(url.createdAt(), 'dd/MM/yyyy')}">
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <!-- 📝 FORMULARIO (th:object - model binding) -->
        <h2>Crear Nueva URL Corta</h2>
        <form th:object="${createShortUrlForm}"
              method="post"
              action="/short-urls"
              class="form-create">

            <!-- Campo: Original URL -->
            <div class="form-group">
                <label for="originalUrl">URL Original:</label>
                <!-- 🎯 TH:FIELD - Bind a propiedad del objeto -->
                <input type="url"
                       id="originalUrl"
                       th:field="*{originalUrl}"
                       class="form-control"
                       placeholder="https://ejemplo.com/url-larga"
                       required>
                <!-- ❌ MOSTRAR ERRORES DE VALIDACIÓN -->
                <span th:if="${#fields.hasErrors('originalUrl')}"
                      th:errors="*{originalUrl}"
                      class="error">
                </span>
            </div>

            <!-- Campo: Privado -->
            <div class="form-group">
                <label>
                    <input type="checkbox"
                           id="isPrivate"
                           th:field="*{isPrivate}">
                    Privada (solo el propietario puede ver)
                </label>
            </div>

            <!-- Campo: Expiración -->
            <div class="form-group">
                <label for="expirationInDays">Expiración (días):</label>
                <input type="number"
                       id="expirationInDays"
                       th:field="*{expirationInDays}"
                       class="form-control"
                       min="1"
                       value="30">
            </div>

            <!-- Botón enviar -->
            <button type="submit" class="btn btn-primary">
                ➕ Acortar URL
            </button>
        </form>
    </div>
</body>
</html>
```

---

## 🚀 Próximos Pasos para tu Refactorización

### Orden recomendado:

1. **Crear estructura de directorios**
   ```bash
   mkdir -p src/main/java/com/manuonda/urlshortener/{shorturl,user,notification,web}/{internal,api}
   ```

2. **Mover archivos existentes** a nuevos directorios
   - `ShortUrlService.java` → `shorturl/internal/`
   - `ShortUrlRepository.java` → `shorturl/internal/`
   - `ShortUrlDto.java` → `shorturl/api/`
   - etc.

3. **Crear eventos públicos**
   - `shorturl/ShortUrlCreatedEvent.java`
   - `user/UserCreatedEvent.java`

4. **Crear módulo user** (similar a shorturl)

5. **Crear módulo notification**
   - Escucha eventos de shorturl y user
   - Envía notificaciones

6. **Actualizar web**
   - Controllers inyectan servicios de otros módulos
   - NO inyectan clases internas

7. **Agregar test de arquitectura**
   ```java
   @Test
   void testModuleStructure() {
       ApplicationModules.of(SpringBootUrlShortenerApplication.class).verify();
   }
   ```

---

## 📚 Resumen Rápido

| Pregunta | Respuesta |
|----------|-----------|
| **¿Quién maneja Thymeleaf?** | Módulo `web` |
| **¿Dónde van los Controllers?** | `web/controller/` |
| **¿Dónde van los Templates?** | `templates/` (en web module) |
| **¿Puede web acceder a shorturl?** | ✅ Sí, a `shorturl/*` (APIs públicas) |
| **¿Puede web acceder a shorturl.internal?** | ❌ No, es privado |
| **¿Puede shorturl acceder a web?** | ❌ No, viola estructura modular |
| **¿Cómo se comunican módulos?** | Application Events (@ApplicationModuleListener) |
| **¿Qué valida los límites?** | Spring Modulith (ApplicationModules.verify()) |

---

**¡Ya tienes todo documentado! Ahora solo necesitas hacer la refactorización siguiendo esta guía.** 🚀