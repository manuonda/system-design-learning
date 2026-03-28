# manejo-http-client

[English →](README.md)

Ejemplo pequeño de **Spring Boot** que llama a **httpbin.org** con **`RestClient`**, traduce errores HTTP con **`defaultStatusHandler`** y muestra **`@Retryable`** del módulo de **resiliencia** de Spring (no es Spring Retry clásico).

### Requisitos

- **Java 21**
- **Maven 3.9+**

### Configuración

| Propiedad | Descripción |
|-----------|-------------|
| `api.url` | URL base de las llamadas salientes (por defecto: `https://httpbin.org`) |
| `server.port` | Puerto del servidor local (por defecto **8080**) |

### Cómo ejecutar

```bash
cd examples/manejo-http-client
mvn spring-boot:run
```

Cuando la aplicación esté arriba, usa los endpoints **locales** siguientes (reenvían peticiones a httpbin).

### Ejemplos HTTP con `curl`

Cambia `8080` si usas otro `server.port`.

```bash
# Llama vía tu app a https://httpbin.org/get — devuelve el JSON de httpbin
curl -s http://localhost:8080/get
```

```bash
# Llama a https://httpbin.org/status/{code}
curl -i http://localhost:8080/get/status/200
```

```bash
# Ejemplo de error remoto (según tu handler: NotFoundException / ApiException)
curl -i http://localhost:8080/get/status/404
```

Con **`jq`** (opcional):

```bash
curl -s http://localhost:8080/get | jq .
```

### Estructura del proyecto (resumen)

- **`RestClientConfig`**: define el bean `RestClient` con `baseUrl` desde `api.url` y `defaultStatusHandler` para respuestas de error.
- **`HttpBinController`**: endpoints REST que usan el `RestClient` inyectado.
- **`ManejoHttpClientApplication`**: `@EnableResilientMethods` activa **`org.springframework.resilience.annotation.Retryable`** en los beans.
- **`getUnstable()`** lleva **`@Retryable`** (reintentos ante `ApiException` según los atributos). **Aún no** tiene `@GetMapping`; para probarlo por HTTP hay que exponer un endpoint o llamarlo desde otro bean.
