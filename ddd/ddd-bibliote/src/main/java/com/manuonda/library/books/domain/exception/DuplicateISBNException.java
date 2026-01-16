package com.manuonda.library.books.domain.exception;

import com.manuonda.library.shared.DomainException;

/**
 * Exception thrown when attempting to register a book with an ISBN
 * that already exists in the system.
 * This is a domain exception that can be used by application
 * and infrastructure layers for error handling.
 *
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 */
public class DuplicateISBNException extends DomainException {

    /**
     * Creates a new DuplicateISBNException with the given message.
     *
     * @param message the detail message explaining the duplicate ISBN conflict
     */
    public DuplicateISBNException(String message) {
        super(message);
    }
}
