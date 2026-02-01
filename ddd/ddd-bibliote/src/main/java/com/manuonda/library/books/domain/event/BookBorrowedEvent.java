package com.manuonda.library.books.domain.event;

public record BookBorrowed(String isbn , int copiesCount) {
}
