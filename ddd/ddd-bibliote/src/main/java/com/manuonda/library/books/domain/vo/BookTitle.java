package com.manuonda.library.books.domain.vo;

import com.manuonda.library.shared.AssertUtil;

/**
 * Value Object representing a Book Title.
 * @author dgarcia
 * @version 1.0
 * @date 14/01/2025
 * @param title
 */
public record BookTitle(String title) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 255;

    public BookTitle {
     AssertUtil.requireNotBlank(title, "Book title cannot be blank");
     AssertUtil.requireSize(title.length(), MIN_LENGTH, MAX_LENGTH, "Book title");
    }

    public static BookTitle parse(String title) {
        return new BookTitle(title);
    }
}
