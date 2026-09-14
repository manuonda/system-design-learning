package com.example.support_assistant;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.springframework.ai.chat.model.ChatModel;

import java.util.List;
import java.util.Map;


/**
 * The starter already put a ChatModel bean in your application context, so you can inject it and send your first request.
 * In this step you build up a call to that bean piece by piece,
 * from a plain string to a full Prompt with options.
 */
@Service
public class SupportAssistantServiceLowChatModel {

    private static final Logger log = LoggerFactory.getLogger(SupportAssistantServiceLowChatModel.class);

    private final ChatModel chatModel;

    SupportAssistantServiceLowChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /*
     * Paso 1: la sobrecarga mas simple de ChatModel.
     * chatModel.call(String) envuelve el texto en un Prompt de un solo UserMessage
     * y devuelve directamente el String de la respuesta, sin metadatos ni opciones.
    String generateResponse(String query) {
        return chatModel.call(query);
    }*/

    /**
     * Paso 2: se agrega un SystemMessage junto al UserMessage.
     * El SystemMessage define la persona/tono del asistente (rol "system"),
     * mientras que el UserMessage lleva la pregunta del usuario (rol "user").
     * chatModel.call(Message...) sigue devolviendo solo el String de la respuesta.

    String generateResponse(String query){
       return chatModel.call(
               new SystemMessage("You are a support agent for the Spring framework. Answer clearly and always include a link to the relevant official docs when one exists, never inventing URLs."),
               new UserMessage(query));

    }
     */

    /**
     * Paso 3 (version activa): Prompt completo con PromptTemplate, ChatOptions y ChatResponse.
     * <p>
     * 1. Se construye el mensaje del usuario con un {@link PromptTemplate}, que rellena el
     *    placeholder {@code {question}} con la consulta recibida, en vez de concatenar Strings.
     * 2. Se arma un {@link Prompt} con el SystemMessage (persona) y el UserMessage (plantilla ya resuelta).
     * 3. Se le pasan {@link OpenAiChatOptions} especificas del proveedor para esta llamada:
     *    se fuerza el modelo "gpt-5.4-mini" y una temperatura 0.0 (respuestas deterministas).
     * 4. chatModel.call(prompt) devuelve el {@link org.springframework.ai.chat.model.ChatResponse}
     *    completo (no solo el texto), que se registra en el log para inspeccionar metadatos
     *    como el modelo usado y el consumo de tokens (usage).
     * 5. Del ChatResponse se extrae el texto final con getResult().getOutput().getText().
     *
     * @param query la pregunta del usuario, tal como llega desde el controller
     * @return el texto de la respuesta generada por el modelo
     */
    String generateResponse(String query){
        var userPromptTemplate = PromptTemplate.builder()
                .template("Answer the following question with a short, well-structured explanation: {question}")
                .variables(Map.of("question", query))
                .build();

        var userMessage = userPromptTemplate.createMessage();

         var prompt = new Prompt(
                 List.of(new SystemMessage("You are a support agent for the Spring framework. Answer clearly and always include a link to the relevant official docs when one exists, never inventing URLs."), userMessage),
                 OpenAiChatOptions.builder()
                         .model("gpt-5.4-mini")
                         .temperature(0.0)
                         .build());

                 var chatResponse = chatModel.call(prompt);
                 log.info("Chat Response : {}", chatResponse);
                 return chatResponse.getResult().getOutput().getText();

    }

    String respuestaInformation(String query ) {
        var userPrompTemplate =  PromptTemplate.builder()
                .template("Eres un experto en pokemon , lo cual buscaremos informacion sobre el pokemon {query}")
                .variables(Map.of("query", query))
                .build();

        var userMessage =  userPrompTemplate.createMessage();

        var
    }
}
