package com.example.http.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;

import com.example.http.client.exception.ApiException;
import com.example.http.client.exception.NotFoundException;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Configuration
public class RestClientConfig {

    
    @Value("${api.url}")
    private String apiUrl;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
          return builder
          .baseUrl(apiUrl)
          .defaultStatusHandler(HttpStatusCode::isError, (HttpRequest req, ClientHttpResponse res) -> {
            if (res.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("Resource not found");
            }
             throw new ApiException("Unexpected error", res.getStatusCode());
           })
          .build();
    }
}
