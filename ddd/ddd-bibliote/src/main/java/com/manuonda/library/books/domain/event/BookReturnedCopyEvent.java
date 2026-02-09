package com.manuonda.library.books.domain.event;

import com.manuonda.library.shared.DomainEvent;

/**
 * Book Returned Copy Event
 * @param isbn
 * @param copiesCount
 */
public record BookReturnedCopyEvent(String isbn, Integer copiesCount) implements DomainEvent {
}
