package com.example.http.client;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

@RestController
public class GlobalExceptionHandler {


    @ExceptionHandler(HttpStatusCodeException.class)
    public ProblemDetail handleHttpStatusCodeException(HttpStatusCodeException ex) {
        return ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getMessage());
    }
}
