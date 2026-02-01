package com.manuonda.library.books.domain.event;

import com.manuonda.library.shared.DomainEvent;

/**
 * Event published when a book is added to the library
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 * @date 1/2/2026
 */
public record AddBookEvent(String isbn, String title, String author, Integer copies) implements DomainEvent {
}
