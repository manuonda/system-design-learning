package com.example.support_assistant;


import com.example.support_assistant.dto.SupportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SupportAssistantController {

    private final SupportAssistantServiceLowChatModel lowLevelService;
    private final SupportAssistanServiceFluentChatClient fluentService;
    private final SupportAssistantServiceOutput structuredOutputService;

    public SupportAssistantController(SupportAssistantServiceLowChatModel lowLevelService,
                                       SupportAssistanServiceFluentChatClient fluentService,
                                       SupportAssistantServiceOutput structuredOutputService) {
        this.lowLevelService = lowLevelService;
        this.fluentService = fluentService;
        this.structuredOutputService = structuredOutputService;
    }

    /**
     * Section 2: low-level ChatModel API.
     * curl -G "http://localhost:8080/api/v1/chat/low-level" --data-urlencode "query=Tell me about Spring AI"
     */
    @GetMapping(path = "/api/v{version}/chat/low-level")
    String chatLowLevel(@RequestParam String query) {
        return lowLevelService.generateResponse(query);
    }

    /**
     * Section 3: fluent ChatClient API.
     * curl -G "http://localhost:8080/api/v1/chat/fluent" --data-urlencode "query=Tell me about Spring AI"
     */
    @GetMapping(path = "/api/v{version}/chat/fluent")
    String chatFluent(@RequestParam String query) {
        return fluentService.generateResponse(query);
    }

    /**
     * Section 4: structured output, the final state of the lab.
     * curl -G "http://localhost:8080/api/v1/chat" --data-urlencode "query=Tell me about Spring AI"
     */
    @GetMapping(path = "/api/v{version}/chat")
    SupportResponse chatStructuredOutput(@RequestParam String query) {
        return structuredOutputService.generateResponse(query);
    }

}
