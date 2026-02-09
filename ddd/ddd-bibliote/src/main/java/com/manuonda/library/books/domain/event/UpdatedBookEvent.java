package com.manuonda.library.books.domain.event;

import com.manuonda.library.shared.DomainEvent;


/**
 * Event published when a book is updated
 * @author dgarcia
 * @version 1.0
 * @since 1.0
 * @date 1/2/2026   
 */
public record UpdatedBookEvent(String isbn, String title, String author, Integer copiesCount) implements DomainEvent {
}
