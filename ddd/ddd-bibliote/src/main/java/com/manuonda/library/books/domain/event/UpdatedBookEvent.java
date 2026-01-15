package com.manuonda.library.books.domain.event;

public record UpdatedBookEvent(String isbn, String title, String author, Integer copies) {
}
