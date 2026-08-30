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
     * ChatClient
    String generateResponse(String query ){
        return chatClient.prompt()
                .user(query)
                .call()
                .content();

    }
    * */

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
