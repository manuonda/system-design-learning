package com.manuonda.library.books.application.dto.response;

import java.util.List;

public record ListBookResponse(
        List<BookResponse> books,
        int totalElements,
        int pageNumber,
        int pageSize,
        int totalPages
) {

    public ListBookResponse(List<BookResponse> books) {
        this(books, books.size(),books.size(),0,0);
    }
}
