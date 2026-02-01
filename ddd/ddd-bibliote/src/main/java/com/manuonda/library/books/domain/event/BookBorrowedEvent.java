package com.manuonda.library.books.domain.event;

import com.manuonda.library.shared.DomainEvent;

/**
 * Event published when a book is borrowed
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 */
public record BookBorrowedEvent(String isbn , Integer copiesCount) implements DomainEvent{
}
