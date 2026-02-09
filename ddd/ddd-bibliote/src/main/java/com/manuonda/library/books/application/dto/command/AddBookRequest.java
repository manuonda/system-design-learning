package com.manuonda.library.books.application.dto.command;

import jakarta.validation.constraints.NotNull;

public record AddBookRequest(
        @NotNull(message = "The isbn is not empty")
        String isbn,
        @NotNull(message = "The title is not empty")
        String title,
        @NotNull(message = "The author is not empty")
        String author,
        @NotNull(message = "The copies is not empty")
        Integer copies) {
}
