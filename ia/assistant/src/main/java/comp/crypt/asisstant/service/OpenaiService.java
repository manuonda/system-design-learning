package comp.crypt.asisstant.service;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class OpenaiService {


    private final ChatClient chatClient;

    public OpenaiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
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
        return chatClient.prompt()
                .user(message)
                .stream()
                .content();
    }
}
