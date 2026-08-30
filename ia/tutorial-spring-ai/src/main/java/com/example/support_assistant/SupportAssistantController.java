package com.example.support_assistant;


import com.openai.models.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportAssistantController {

    private final SupportAssistantServiceLowChatModel service;

    public SupportAssistantController(SupportAssistantServiceLowChatModel supportAssistantServiceLowChatModel) {
        this.service = supportAssistantServiceLowChatModel;
    }

    /**
     * curl -G "http://localhost:8080/api/v1/chat" --data-urlencode "query=Tell me about Spring AI"
     * @param query
     * @return
     */
    @GetMapping(path = "/api/v{version}/chat")
    String chat2(@RequestParam String query){
        return service.generateResponse(query);
    }

    @GetMapping(path = "/api/v{version}/chat")
    SupportResponse chatStructuredOutput(@RequestParam String query) {
        return service.generateResponse(query);
    }

}
