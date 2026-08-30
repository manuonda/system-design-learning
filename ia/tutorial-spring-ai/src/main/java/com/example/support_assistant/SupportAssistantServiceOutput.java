package com.example.support_assistant;


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

    SupportResponse generateResponse(String query) {
        return chatClient.prompt()
                .user(u -> u
                        .text("Answer the following question with a short, well-structured explanation: {question}")
                        .param("question", query))
                .call()
                .entity(SupportResponse.class);
    }
}
