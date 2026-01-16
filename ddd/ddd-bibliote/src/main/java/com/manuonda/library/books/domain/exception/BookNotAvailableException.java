package com.manuonda.library.books.domain.exception;

import com.manuonda.library.shared.DomainException;

/**
 * Exception thrown when attempting to borrow a book that has no available copies.
 * This is a domain exception that can be used by application
 * and infrastructure layers for error handling.
 *
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 */
public class BookNotAvailableException extends DomainException {

    /**
     * Creates a new BookNotAvailableException with the given message.
     *
     * @param message the detail message explaining why the book is not available
     */
    public BookNotAvailableException(String message) {
        super(message);
    }
}
