# manejo-http-client

[Español →](README.sp.md)

Small **Spring Boot** sample that calls **httpbin.org** with **`RestClient`**, maps HTTP errors to domain exceptions via **`defaultStatusHandler`**, and shows **resilience `@Retryable`** (Spring Framework resilience annotations, not Spring Retry).

### Requirements

- **Java 21**
- **Maven 3.9+** (or use the wrapper if you add one)

### Configuration

| Property | Description |
|----------|-------------|
| `api.url` | Base URL for outgoing calls (default in `application.properties`: `https://httpbin.org`) |
| `server.port` | Local server port (default **8080** if unset) |

### Run the application

```bash
cd examples/manejo-http-client
mvn spring-boot:run
```

Wait until the app has started, then call the **local** endpoints below (your app exposes these; they forward to httpbin).

### HTTP examples (`curl`)

Replace `8080` if you set `server.port`.

```bash
# Proxies to https://httpbin.org/get — returns JSON from httpbin
curl -s http://localhost:8080/get
```

```bash
# Proxies to https://httpbin.org/status/{code} — returns 200 with a short success body if httpbin returns that status
curl -i http://localhost:8080/get/status/200
```

```bash
# Example: trigger a client error on the remote side (handled by your RestClient defaultStatusHandler → ApiException / NotFoundException)
curl -i http://localhost:8080/get/status/404
```

Optional: pretty-print JSON (if `jq` is installed):

```bash
curl -s http://localhost:8080/get | jq .
```

### Project layout (basics)

- **`RestClientConfig`**: builds a `RestClient` bean with `baseUrl` from `api.url` and `defaultStatusHandler` for error responses.
- **`HttpBinController`**: REST endpoints that use the injected `RestClient`.
- **`ManejoHttpClientApplication`**: `@EnableResilientMethods` enables **`org.springframework.resilience.annotation.Retryable`** on Spring beans.
- **`getUnstable()`** in the controller is annotated with **`@Retryable`** (retries on `ApiException` per your attributes). It is **not** mapped to an HTTP route yet; call it from another bean or add a `@GetMapping` if you want to try it via `curl`.
