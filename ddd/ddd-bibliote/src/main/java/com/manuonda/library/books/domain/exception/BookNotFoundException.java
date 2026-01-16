package com.manuonda.library.books.domain.exception;

import com.manuonda.library.shared.DomainException;

/**
 * Exception thrown when a book cannot be found in the system.
 * This is a domain exception that can be used by application
 * and infrastructure layers for error handling.
 *
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 */
public class BookNotFoundException extends DomainException {

    /**
     * Creates a new BookNotFoundException for the given ISBN.
     *
     * @param isbn the ISBN of the book that was not found
     */
    public BookNotFoundException(String isbn) {
        super("Book with ISBN " + isbn + " not found");
    }
}
