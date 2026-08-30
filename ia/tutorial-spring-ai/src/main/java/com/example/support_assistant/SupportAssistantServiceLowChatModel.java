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
    String generateResponse(String query) {
        return chatModel.call(query);
    }*/

    /**
     * System Message, UserMessage

    String generateResponse(String query){
       return chatModel.call(
               new SystemMessage("You are a support agent for the Spring framework. Answer clearly and always include a link to the relevant official docs when one exists, never inventing URLs."),
               new UserMessage(query));

    }
     */

    /** Prompt Template Full Prompt with ChatOptions and ChatResponse
     *
     * @param query
     * @return
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
}
