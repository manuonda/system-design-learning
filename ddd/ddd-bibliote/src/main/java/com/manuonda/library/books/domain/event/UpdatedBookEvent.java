package com.manuonda.library.books.domain.event;

import com.manuonda.library.shared.DomainEvent;

public record UpdatedBookEvent(String isbn, String title, String author, Integer copies) implements DomainEvent {
}
