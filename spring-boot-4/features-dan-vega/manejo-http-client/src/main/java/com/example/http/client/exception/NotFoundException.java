package com.example.http.client.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class NotFoundException extends ApiException {

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

    public NotFoundException(String message, HttpStatusCode statusCode) {
        super(message, statusCode);
    }

}
