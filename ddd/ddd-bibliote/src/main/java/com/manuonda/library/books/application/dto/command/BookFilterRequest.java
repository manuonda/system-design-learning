package com.manuonda.library.books.application.dto.command;

public record BookFilterRequest(
        String isbn,
        int page,
        int size
) {
}
