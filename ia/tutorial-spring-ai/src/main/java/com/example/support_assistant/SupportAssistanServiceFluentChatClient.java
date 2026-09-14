package com.example.support_assistant;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class SupportAssistanServiceFluentChatClient {

    private static final Logger log = LoggerFactory.getLogger(SupportAssistanServiceFluentChatClient.class);

    private final ChatClient chatClient;

    SupportAssistanServiceFluentChatClient(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /*
     * Version minima con ChatClient: la cadena fluida prompt().user().call().content()
     * reemplaza todo el armado manual de Prompt/ChatOptions de la version con ChatModel.
     * .content() es un atajo que devuelve directamente el String de la respuesta.
    String generateResponse(String query ){
        return chatClient.prompt()
                .user(query)
                .call()
                .content();

    }
    * */

    /**
     * Version activa: ChatClient con template inline en el mensaje de usuario y ChatResponse completo.
     * <p>
     * - El paso {@code .user(u -> ...)} usa el builder propio del mensaje de usuario para poner
     *   una plantilla con placeholder {@code {question}} directamente en la cadena fluida
     *   (equivalente al PromptTemplate usado en la version de bajo nivel, pero sin construirlo aparte).
     * - {@code .system(...)} esta comentado porque el prompt de sistema ya no se repite aca:
     *   se configura una sola vez como default en el bean {@code ChatClient} de
     *   {@link SupportAssistantConfiguration}, y aplica automaticamente a toda llamada de este cliente.
     * - Se pide {@code .chatResponse()} en vez de {@code .content()} para poder loguear la respuesta
     *   completa (metadatos, usage) antes de devolver solo el texto al llamador.
     *
     * @param query la pregunta del usuario, tal como llega desde el controller
     * @return el texto de la respuesta generada por el modelo
     */
    String generateResponse(String query ) {
        var chatResponse =  this.chatClient.prompt()
                //.system("You are a support agent for the Spring framework. Answer clearly and always include a link to the relevant official docs when one exists, never inventing URLs.")
                .user( u -> u
                        .text("Answer the following question with a short, well-structured explanation: {question}")
                        .param("question", query))
                .call()
                .chatResponse();
        log.info("Chat response: {}", chatResponse);
        return chatResponse.getResult().getOutput().getText();
    }




}
