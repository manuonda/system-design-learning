package com.manuonda.library.books.domain.dto;


/**
 * Class BookSearch Criteria
 * @param isbn
 * @param page
 * @param size
 */
public record BookSearchCriteria(
        String isbn,
        int page,
        int size
) {
}
