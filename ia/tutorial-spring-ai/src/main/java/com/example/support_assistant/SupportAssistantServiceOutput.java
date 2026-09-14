package com.example.support_assistant;


import com.example.support_assistant.dto.SupportResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;

@Service
public class SupportAssistantServiceOutput {

    private static final Logger log = LoggerFactory.getLogger(SupportAssistantServiceOutput.class);

    private final ChatClient chatClient;

    SupportAssistantServiceOutput(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /*
     * Paso previo: few-shot prompting manual.
     * Se escriben a mano las reglas de formato JSON y un par de ejemplos en el system prompt,
     * confiando en que el modelo "copie el patron" para la pregunta real. Sigue devolviendo
     * un String crudo: el parseo a objeto Java quedaria por cuenta de quien llama.
    String generateResponse(String query) {
        var chatResponse = chatClient.prompt()
                .system("""
                  You are a Spring support classifier.
                  Reply only with JSON in this form:
                  {"category":"...","answer":"..."}
                  The category must be one of: TECHNICAL, BILLING, SECURITY, GENERAL.
                  Examples:
                  - "Why was I billed twice?"     -> {"category":"BILLING","answer":"..."}
                  - "How do I rotate my API key?" -> {"category":"SECURITY","answer":"..."}
                  """)
                .user(query)
                .call()
                .chatResponse();
        log.info("Chat Response {}", chatResponse);
        return chatResponse.getResult().getOutput().getText();
    }
    */

    /**
     * Version activa: salida estructurada con {@code .entity(...)}.
     * <p>
     * En vez de parsear JSON a mano, Spring AI genera el schema a partir del record
     * {@link SupportResponse} (usando las anotaciones {@code @JsonPropertyDescription}),
     * se lo agrega al pedido y deserializa la respuesta directamente al tipo Java indicado.
     * Como el bean {@code ChatClient} en {@link SupportAssistantConfiguration} tiene habilitado
     * {@code AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT}, el proveedor ademas garantiza
     * (via su propia API de structured output) que el JSON siempre cumpla ese schema.
     *
     * @param query la pregunta del usuario, tal como llega desde el controller
     * @return un {@link SupportResponse} ya deserializado, con la categoria y la respuesta
     */
    SupportResponse generateResponse(String query) {
        return chatClient.prompt()
                .user(u -> u
                        .text("Answer the following question with a short, well-structured explanation: {question}")
                        .param("question", query))
                .call()
                .entity(SupportResponse.class);
    }
}
