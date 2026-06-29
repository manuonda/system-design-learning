package comp.crypt.asisstant.controller;


import comp.crypt.asisstant.service.OpenaiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class OpenaiController {

    private final OpenaiService openaiService;

    public OpenaiController(OpenaiService openaiService) {
        this.openaiService = openaiService;
    }

    @GetMapping("/ask")
    public String ask(@RequestParam String message) {
        return openaiService.ask(message);
    }

    @GetMapping("/stream")
    public Flux <String> stream(@RequestParam String message) {
        return openaiService.stream(message);
    }

}
