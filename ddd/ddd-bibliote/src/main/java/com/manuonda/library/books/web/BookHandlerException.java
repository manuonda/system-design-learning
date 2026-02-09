package com.manuonda.library.books.web;


import com.manuonda.library.books.domain.exception.BookNotAvailableException;
import com.manuonda.library.books.domain.exception.BookNotFoundException;
import com.manuonda.library.books.domain.exception.DuplicateISBNException;
import com.manuonda.library.shared.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class BookHandlerException {

    private static final Logger logger = LoggerFactory.getLogger(BookHandlerException.class);

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleBookNotFound(BookNotFoundException ex, HttpServletRequest request) {
        logger.error("Book not found: {}", ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                "BOOK_NOT_FOUND",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicateISBNException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateISBN(DuplicateISBNException ex, HttpServletRequest request) {
        logger.error("Duplicate ISBN: {}", ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                "DUPLICATE_ISBN",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleBookNotAvailable(BookNotAvailableException ex, HttpServletRequest request) {
        logger.error("Book not available: {}", ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
                "BOOK_NOT_AVAILABLE",
                ex.getMessage(),
                LocalDateTime.now(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}