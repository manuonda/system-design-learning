package comp.crypt.asisstant.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * MessageChatMemoryAdvisor
 */

@Service
public class OpenaiService {


    private static final String CONVERSATION_ID="manuonda";

    @Value("classpath:prompts/system-prompt.st")
    private Resource systemPromptResource;

    private final ChatClient chatClient;
    //private final ChatClient chatClientMemoryRepository;


    public OpenaiService(ChatClient.Builder chatClientBuilder, ChatClient.Builder chatClientMemoryRepository) {

        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(
                                MessageWindowChatMemory.builder()
                                        .maxMessages(10)
                                        .build()
                        ).build())
                .build();

       // this.chatClientMemoryRepository = chatClientMemoryRepository
       //         .defaultAdvisors();
    }


    public String ask(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    /**
     *
     * @param message
     * @return
     */
    public Flux<String> stream(String message) {
        Prompt prompt = buildPrompt(message);
        /*return chatClient.prompt()
                .user(message)
                .stream()
                .content();

         */
        return chatClient
                .prompt(prompt)
                .stream()
                .content();
    }

    private Prompt buildPrompt(String message){
        try
        {
          String systemPrompt = this.systemPromptResource.getContentAsString(StandardCharsets.UTF_8);
          return new Prompt(
                  List.of(
                          new SystemMessage(systemPrompt),
                          new UserMessage(message)
                  )
          );
        }catch (IOException e){
            throw new UncheckedIOException("Failed to load system prompt", e);
        }
    }
}
