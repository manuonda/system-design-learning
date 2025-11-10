package com.manuonda.urlshortener.domain.exceptions;

public class ClickLimitExceededException extends RuntimeException {
    public ClickLimitExceededException(String message) {
        super(message);
    }
    public ClickLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
