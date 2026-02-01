package com.manuonda.library.books.domain.event;

import com.manuonda.library.shared.DomainEvent;

/**
 * Book Returned Copy Event
 * @param isbn
 * @param value
 */
public record BookReturnedCopyEvent(String isbn, Integer value) implements DomainEvent {
}
