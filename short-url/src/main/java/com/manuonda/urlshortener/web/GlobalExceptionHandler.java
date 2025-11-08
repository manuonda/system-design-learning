package com.manuonda.urlshortener.web;

import com.manuonda.urlshortener.domain.exceptions.ShortUrlNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler  {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ShortUrlNotFoundException.class)
    String handleShortUrlNotFoundExc(ShortUrlNotFoundException ex){
        log.error(ex.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    String handleGenericExc(Exception ex){
        log.error(ex.getMessage());
        return "error/500";
    }
}
