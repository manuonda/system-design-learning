# Fundamentos de Spring AI: ChatModel y ChatClient

*(Resumen en español de `SpringAIFundamentals.md`)*

Por muy sofisticados que sean, los modelos de lenguaje (LLM) se consumen igual que cualquier otro servicio en la nube: por HTTP. Envías un JSON con tu prompt y algunas opciones al endpoint del proveedor, y recibes de vuelta un JSON con el texto generado, el conteo de tokens y metadatos. Anthropic, Mistral, OpenAI y Ollama siguen este mismo modelo de petición/respuesta, cada uno con sus propios detalles específicos.

Esto es una buena noticia porque integrarse con APIs REST es justamente lo que Spring siempre ha hecho bien. El ecosistema ya te da clientes HTTP, serialización JSON, *connection pooling*, reintentos y configuración externalizada. En principio podrías llamar a un modelo tú mismo con un `RestClient`, construir el cuerpo de la petición a mano, parsear la respuesta y gestionar tu API key.

Podrías, pero rápidamente te encontrarías reimplementando mucha "plomería":

- Construir y parsear a mano el JSON específico de cada proveedor.
- Reescribir ese código cada vez que cambias o añades un proveedor.
- Montar tú mismo streaming, reintentos, manejo de errores y observabilidad.
- Mapear las respuestas crudas a objetos de dominio, mantener el historial de conversación, etc.

Este es el hueco que llena Spring AI. Se apoya en las mismas bases HTTP, pero te da una abstracción diseñada para hablar con modelos, de modo que describes **qué** quieres preguntar en vez de **cómo** formatear la llamada HTTP. Cambiar de proveedor pasa a ser un cambio de configuración, no una reescritura.

Si has usado Spring Data, esto te resultará familiar: Spring Data te da un modelo de programación consistente sobre distintos almacenes de datos, y Spring AI te da un modelo de programación consistente sobre distintos proveedores y modelos de IA, sin dejar de poder acceder a opciones específicas de cada proveedor cuando lo necesitas.

Spring AI ofrece ese modelo de programación mediante dos abstracciones complementarias:

- **`ChatModel`** (bajo nivel): una API directa que gestiona la llamada HTTP, serializa tu petición y mapea el JSON crudo del proveedor de vuelta a objetos Java.
- **`ChatClient`** (alto nivel): construida encima de `ChatModel` para un uso diario más sencillo y capacidades más avanzadas.

## Dependencias y configuración

Antes de escribir código necesitas el *starter* adecuado. Spring AI publica un Spring Boot starter dedicado por proveedor. Para OpenAI:

```xml
<!-- Maven: pom.xml -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

```groovy
// Gradle: build.gradle
implementation 'org.springframework.ai:spring-ai-starter-model-openai'
```

Otros proveedores siguen la misma convención de nombres, por ejemplo `spring-ai-starter-model-anthropic` o `spring-ai-starter-model-ollama`.

También conviene importar el BOM de Spring AI para que todos los artefactos resuelvan a una versión consistente y compatible (y así puedes omitir versiones explícitas en los starters):

```xml
<!-- Maven: pom.xml -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>2.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```groovy
// Gradle: build.gradle
dependencies {
    implementation platform('org.springframework.ai:spring-ai-bom:2.0.1')
}
```

El starter trae la implementación del proveedor junto con la autoconfiguración de Spring Boot, que crea los beans necesarios por ti.

El resto es configuración, en `application.properties` o `application.yml`, como cualquier otra propiedad de Spring Boot. Cada proveedor tiene su propio espacio de nombres (`spring.ai.openai.*`, `spring.ai.ollama.*`, etc.), pero todos exponen tipos de ajustes similares. Ejemplo con OpenAI:

```properties
# Autenticación
spring.ai.openai.api-key=${OPENAI_API_KEY}
# El endpoint; puedes apuntarlo a un mock, un gateway, o una API compatible
spring.ai.openai.base-url=https://api.openai.com
# El modelo a usar, por ejemplo gpt-5.5 o gpt-5.4-mini
spring.ai.openai.chat.model=gpt-5.6-sol
# Parámetros de la petición, como la aleatoriedad del muestreo. Menor = más determinista, mayor = más creativo
spring.ai.openai.chat.temperature=0.7
spring.ai.openai.chat.reasoning-effort=none
```

Como el modelo y demás opciones viven fuera de tu código, puedes ajustar el comportamiento o cambiar de modelo sin tocar ni una sola clase.

### Combinar varios proveedores en una misma aplicación

Cada proveedor tiene su propio namespace y su propio starter, así que puedes usar varios a la vez (por ejemplo uno para chat y otro para generación de imágenes). Añades ambos starters y configuras ambos. Cuando más de un starter en el classpath puede servir el mismo tipo de modelo, las propiedades `spring.ai.model.*` deciden cuál se encarga:

```properties
spring.ai.model.chat=ollama
spring.ai.model.image=openai
```

También puedes variar de proveedor según el entorno usando *profiles* de Spring (por ejemplo un modelo local en `application-dev.properties` y uno alojado en `application-prod.properties`). Y si necesitas control total, puedes desactivar la autoconfiguración y montar los beans del modelo tú mismo.

## La API de bajo nivel: `ChatModel`

`ChatModel` es la interfaz fundamental que implementa cada proveedor de chat, como `OpenAiChatModel`, `AnthropicChatModel` y `OllamaChatModel`. Es deliberadamente minimalista: le pasas un `Prompt` y te devuelve un `ChatResponse`.

```java
ChatResponse response = chatModel.call(new Prompt("Tell me about Spring AI"));
String text = response.getResult().getOutput().getText();
```

Gracias a la autoconfiguración del starter, ese `ChatModel` ya es un bean de Spring, así que puedes inyectarlo en cualquier componente y llamarlo directamente sin construirlo tú mismo.

Como comodidad, `call()` también acepta un `String` plano, que Spring AI envuelve en un `Prompt` por ti. Esta sobrecarga también simplifica el otro extremo: en vez de un `ChatResponse` que hay que desenvolver, te devuelve el contenido de la respuesta directamente como `String`:

```java
String text = chatModel.call("Tell me about Spring AI");
```

### Prompts, mensajes y roles

Ese atajo esconde lo que realmente es un `Prompt`. Por debajo, un `Prompt` contiene una lista ordenada de objetos `Message`, y cada mensaje lleva uno de los tres roles que definen las APIs subyacentes: `"system"`, `"user"` o `"assistant"`. Estos roles estructuran la conversación y determinan cómo el modelo interpreta cada mensaje.

- **`"system"`** define el comportamiento y tono general del modelo, normalmente al inicio de la conversación → `SystemMessage`.
- **`"user"`** representa el lado humano: preguntas, instrucciones, entradas → `UserMessage`.
- **`"assistant"`** representa las respuestas del modelo; incluirlo en un prompt aporta un turno anterior como contexto → `AssistantMessage`.

Como `Prompt` también acepta una lista de `Message`, puedes componerlos explícitamente:

```java
Prompt prompt = new Prompt(List.of(
    new SystemMessage("You are a support agent for the Spring framework. Answer clearly and always include a link to the relevant official docs when one exists, never inventing URLs."),
    new UserMessage("Tell me about Spring AI")));
ChatResponse response = chatModel.call(prompt);
```

### Imágenes y otros medios en un prompt

Un `UserMessage` no tiene por qué ser solo texto. Muchos modelos modernos son multimodales: pueden recibir más de un tipo de contenido a la vez, típicamente texto junto con imágenes, y algunos también aceptan audio o vídeo. Para un asistente de soporte esto es útil de inmediato: un usuario puede adjuntar una captura de un error en vez de intentar describirlo con palabras.

Spring AI lo soporta con el campo `media` de `UserMessage`. El texto va en el contenido habitual, y las imágenes, audio u otros adjuntos van junto a él como uno o varios objetos `Media`. Cada `Media` combina el contenido crudo (un `Resource` o una `URI`) con un `MimeType` que indica al proveedor qué tipo de dato es. Los medios solo se permiten en mensajes de usuario, porque representan entrada humana; los mensajes de sistema y de asistente siguen siendo solo texto.

```java
var screenshot = new ClassPathResource("/error-dialog.png");

var userMessage = UserMessage.builder()
    .text("What does this error mean, and how do I fix it?")
    .media(new Media(MimeTypeUtils.IMAGE_PNG, screenshot))
    .build();

ChatResponse response = chatModel.call(new Prompt(userMessage));
```

Qué modalidades funcionan depende del proveedor y del modelo concreto. El entendimiento de imágenes es lo más extendido, disponible en los modelos con capacidad de visión de proveedores como OpenAI, Anthropic, Google Gemini, Amazon Bedrock, Mistral y Ollama; audio y vídeo están disponibles en un conjunto más reducido.

### Prompts reutilizables con `PromptTemplate`

En la práctica, los prompts casi nunca son cadenas fijas, porque dependen de datos en tiempo de ejecución. `PromptTemplate` te permite escribir un mensaje con variables `{placeholder}` y rellenarlas en el momento de la llamada, manteniendo tus prompts reutilizables en lugar de construidos por concatenación de strings.

```java
PromptTemplate promptTemplate = PromptTemplate.builder()
    .template("Tell me about {topic}")
    .variables(Map.of("topic", "Spring AI"))
    .build();
ChatResponse response = chatModel.call(promptTemplate.create());
```

Si la sintaxis de placeholder por defecto choca con tus datos, puedes configurar un *renderer* de plantillas personalizado que use un delimitador distinto.

### Cambiar el modelo y otras opciones con `ChatOptions`

Un `Prompt` es más que sus mensajes: también lleva un conjunto de `ChatOptions`, como el nombre del modelo y `maxTokens`. Puedes definirlos como valores por defecto en la configuración y luego sobrescribirlos en una llamada individual, cambiando el modelo para una sola petición sin afectar la configuración global.

> **Nota.** Desde Spring AI 2.0 la API de bajo nivel `ChatModel` requiere opciones específicas del proveedor. Usa el builder del proveedor, como `OpenAiChatOptions.builder()`, en lugar del portable `ChatOptions.builder()`.

```java
ChatResponse response = chatModel.call(new Prompt(
    "Tell me about Spring AI",
    OpenAiChatOptions.builder().model("gpt-5.4-mini").build()));
```

### Qué viene en el `ChatResponse`

El `ChatResponse` envuelve uno o más objetos `Generation`. Un `Generation` es una única propuesta de finalización: el mensaje del asistente junto con metadatos como su razón de finalización. La mayoría de peticiones devuelven exactamente una, por eso el atajo `getResult()` es tan común, aunque puedes pedirle al modelo varias alternativas.

Además del texto generado, `ChatResponse` expone metadatos de la llamada a través de `getMetadata()`, incluyendo el modelo que sirvió la petición y, más importante, `getUsage()`, que da los conteos de tokens del prompt y de la finalización. Los proveedores cobran por token, así que estos conteos son la base para el monitoreo de costes y la presupuestación.

### Llamadas bloqueantes y en streaming

El método `call()` visto hasta ahora es bloqueante: espera toda la finalización antes de devolver. Los modelos generan texto token a token, así que también puedes consumir la respuesta como un stream y mostrar cada fragmento en cuanto se produce. Esto es lo que da el efecto de "máquina de escribir" en los chatbots y mejora mucho la sensación de respuesta en respuestas largas. Para esto, Spring AI ofrece la interfaz complementaria `StreamingChatModel`, cuyo método `stream()` devuelve un `Flux<ChatResponse>` reactivo de respuestas parciales.

### El mismo patrón más allá del chat

Este patrón de bajo nivel no se limita al chat. Spring AI define una interfaz de modelo equivalente para cada modalidad que soporta: `ImageModel` para generación de imágenes, además de `EmbeddingModel`, `AudioTranscriptionModel` y otras. Todas comparten la misma forma —un objeto de petición que entra, uno de respuesta que sale—, así que una vez entiendes `ChatModel` el resto de la familia resulta familiar.

En resumen: `ChatModel` es el contrato portable que esconde la API REST de cada proveedor detrás de una única interfaz Java. Trabaja directamente con objetos `Prompt` y `ChatResponse` y no tiene opinión sobre composición de mensajes, valores por defecto ni preocupaciones transversales. Úsalo cuando quieras control total y explícito.

## La API fluida recomendada: `ChatClient`

`ChatClient` es la API de alto nivel diseñada para el uso diario. Envuelve un `ChatModel` y añade un builder fluido, de modo que compones un prompt, invocas al modelo y das forma a la respuesta en una única cadena legible.

```java
String answer = chatClient.prompt()      // empieza a construir la petición
    .user("Tell me about Spring AI")     // añade el mensaje del usuario
    .call()                              // envía la petición (bloqueante)
    .content();                          // extrae el texto de la respuesta
```

Igual que con `ChatModel`, no construyes un `ChatClient` desde cero: la autoconfiguración del starter te da un bean `ChatClient.Builder` listo para inyectar. Lo que añade el builder es un lugar donde configurar valores por defecto aplicables a cada llamada hecha a través de ese cliente, como un *system prompt* por defecto.

Un patrón habitual es ensamblar un `ChatClient` configurado como `@Bean`, de modo que esos valores por defecto vivan en un único lugar y el resto del código solo inyecte el cliente ya terminado.

```java
@Configuration
class ChatConfiguration {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder
            .defaultSystem("You are a support agent for the Spring framework. Answer clearly and always include a link to the relevant official docs when one exists, never inventing URLs.")
            .build();
    }
}
```

No estás limitado a un único cliente: `ChatClient.Builder` es solo un bean, así que puedes inyectarlo donde lo necesites y hacer `build()` de un `ChatClient` distinto por clase o por caso de uso, cada uno con su propio *system prompt* y opciones.

Al igual que la API `ChatModel`, `ChatClient` soporta tanto llamadas bloqueantes como en streaming desde la misma cadena `prompt()`. Usa `.call()` para esperar la respuesta completa, o `.stream()` para recibir un `Flux<>` reactivo que emite tokens a medida que se producen.

```java
String answer = chatClient.prompt()
    .user(query)
    .call() // bloqueante
    .content();

Flux<String> stream = chatClient.prompt()
    .user(query)
    .stream() // streaming
    .content();
```

Llamar a `.content()` es un atajo que devuelve el `String` del contenido de la respuesta. Cuando necesitas más información, puedes pedir el `ChatResponse` completo:

```java
ChatResponse response = chatClient.prompt()
    .user(query)
    .call()
    .chatResponse();
```

La entrada multimodal de la sección anterior funciona igual aquí. El paso `user` de la cadena tiene su propio builder, así que adjuntas una imagen junto al texto sin construir un `UserMessage` tú mismo.

```java
String answer = chatClient.prompt()
    .user(u -> u
        .text("What does this error mean, and how do I fix it?")
        .media(MimeTypeUtils.IMAGE_PNG, new ClassPathResource("/error-dialog.png")))
    .call()
    .content();
```

## De texto a objetos Java con salida estructurada (Structured Output)

Hasta ahora toda respuesta ha vuelto como texto plano. Eso está bien para un chatbot, pero en una aplicación empresarial normalmente quieres hacer algo con la respuesta del modelo: guardarla, validarla, mostrarla en una UI o pasarla a otra lógica de negocio. El texto libre encaja mal ahí, por eso la salida estructurada es una de las funcionalidades más importantes de Spring AI. Probablemente la usarás en casi cualquier aplicación real.

### Un breve rodeo por *prompt engineering*

¿Cómo obtendrías datos estructurados de un modelo sin soporte del framework? La única forma de influir en un modelo es a través del prompt, así que la respuesta está en el *prompt engineering*: la práctica de redactar y estructurar prompts para dirigir al modelo hacia el resultado que quieres.

Una de las técnicas más efectivas es el *few-shot prompting*: en vez de solo describir el resultado que quieres, le muestras al modelo unos cuantos ejemplos de él. Los modelos son excelentes continuando patrones, así que unos pares de pregunta/respuesta de ejemplo en el formato correcto hacen que el modelo siga ese formato para la pregunta real. Puedes usar esto para que un modelo responda con JSON.

```java
String json = chatClient.prompt()
    .system("""
        You are a Spring support classifier.
        Reply only with JSON in this form:
        {"category":"...","answer":"..."}
        The category must be one of: TECHNICAL, BILLING, SECURITY, GENERAL.
        Examples:
        - "Why was I billed twice?"     -> {"category":"BILLING","answer":"..."}
        - "How do I rotate my API key?" -> {"category":"SECURITY","answer":"..."}
        """)
    .user("Tell me about Spring AI")
    .call()
    .content();
```

Esto funciona, y el *few-shot prompting* sigue siendo una técnica valiosa para dirigir el comportamiento del modelo mucho más allá del formateo. Pero para datos estructurados deja las partes tediosas en tus manos: escribes las instrucciones de formato a mano, tienes que mantener los ejemplos sincronizados con tus tipos Java, y sigues obteniendo un `String` crudo que debes deserializar tú mismo, sin garantía de que el modelo siguió el formato.

### Dejar que Spring AI lo gestione con `.entity(...)`

Este es exactamente el trabajo repetitivo que elimina el soporte de salida estructurada de Spring AI. En vez de devolver texto crudo, `.call()` puede mapear la salida del modelo directamente a un tipo Java mediante `.entity(...)`.

```java
enum SupportCategory { TECHNICAL, BILLING, SECURITY, GENERAL }

record SupportResponse(SupportCategory category, String answer) {}

SupportResponse answer = chatClient.prompt()
    .user("Tell me about Spring AI")
    .call()
    .entity(SupportResponse.class);
```

Por debajo, Spring AI hace lo mismo que harías tú a mano: añade instrucciones de formato a tu prompt indicando al modelo que responda en JSON acorde a la estructura de tu tipo (generadas a partir del propio tipo Java), y luego deserializa el resultado en el objeto por ti. Obtienes datos estructurados y con seguridad de tipos que tu aplicación puede usar directamente, sin parseo manual ni manejo frágil de strings. La frontera entre el código de IA y el resto de tu aplicación Spring desaparece, porque el modelo se convierte en un colaborador más que devuelve objetos de dominio.

El método `.entity(...)` no se limita a *records* planos: también gestiona tipos anidados y colecciones, como una `List` de tus objetos de dominio.

### Salida estructurada nativa (Native structured output)

El enfoque anterior está basado en prompt: Spring AI añade las instrucciones de formato al prompt y confía en que el modelo las siga. Funciona con cualquier modelo, pero sigue siendo solo una petición, así que el modelo a veces puede devolver texto que no parsea.

Muchos proveedores ofrecen ahora una garantía más fuerte llamada *native structured output*. En vez de pedirlo en el prompt, Spring AI envía el JSON schema de tu tipo a la API de salida estructurada del proveedor, y el proveedor restringe al modelo para que la respuesta sea siempre un JSON válido que coincida con el schema. Esto es más fiable y mantiene las instrucciones de formato fuera del prompt. OpenAI, Anthropic, Google Gemini, Mistral y Ollama lo soportan en sus modelos más recientes, cada uno a través de su propia API.

Como el soporte varía según proveedor y modelo, no está activado por defecto. También hay limitaciones específicas de cada proveedor a tener en cuenta. El modo nativo de OpenAI, por ejemplo, no permite un array de nivel superior, así que hay que devolver un *record* que envuelva la `List` en vez de una `List` directamente. Y no todos los modelos de Ollama respetan el schema de forma fiable.

Se activa de dos formas:

**1. A través del spec de `.entity(...)`:**

```java
SupportResponse answer = chatClient.prompt()
    .user("Tell me about Spring AI")
    .call()
    .entity(SupportResponse.class, spec -> spec
        .useProviderStructuredOutput());
```

**2. Usando directamente la API de Advisors**, sobre la que está construido el soporte de salida estructurada de Spring AI (cubierta en detalle en otra sección). Se puede configurar como valor por defecto en el bean `ChatClient`, o adjuntar a una única petición.

```java
ChatClient chatClient(ChatClient.Builder builder) {
    return builder
        .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
        .build();
}

SupportResponse answer = chatClient.prompt()
    .user("Tell me about Spring AI")
    .advisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
    .call()
    .entity(SupportResponse.class);
```

---

### En este artículo

- Dependencias y configuración
- La API de bajo nivel `ChatModel`
- La API fluida recomendada `ChatClient`
- De texto a objetos Java con salida estructurada
