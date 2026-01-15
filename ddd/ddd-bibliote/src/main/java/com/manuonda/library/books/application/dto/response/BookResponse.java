package com.manuonda.library.books.application.dto.response;

public record BookResponse(String isbn, String title, String author, Integer copies) {
}
