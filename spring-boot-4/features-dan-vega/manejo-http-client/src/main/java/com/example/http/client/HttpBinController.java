package com.example.http.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import com.example.http.client.exception.ApiException;



@RestController
public class HttpBinController {

   
    private final RestClient restClient;
    private static final Logger logger = LoggerFactory.getLogger(HttpBinController.class);

    public HttpBinController(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/get")
    public String get() {
        return restClient.get()
                .uri("get")
                .retrieve()
                .body(String.class);
    }

    @GetMapping("get/status/{code}")
    public ResponseEntity<String> getStatusCode(@PathVariable Integer code ) {
     ResponseEntity<Void> response = restClient.get()
        .uri("get/status/{code}", code)
        .retrieve()
        // .onStatus(HttpStatusCode::isError, (HttpRequest req, ClientHttpResponse res) -> {
        //     System.out.println("Error: " + res.getStatusCode());
        // })
        .toBodilessEntity();

        return ResponseEntity.ok("Success : " + response.getStatusCode().value());

    }

    @Retryable(includes = ApiException.class , maxRetries = 3, delay = 1000L, multiplier = 2)
    public String getUnstable(){
        logger.info("Calling unstable endpoint");
        return restClient.get()
        .uri("/unstable")
        .retrieve()
        .body(String.class);
    }
    
}
