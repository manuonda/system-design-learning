package com.manuonda.library.books.domain.vo;

import com.manuonda.library.shared.AssertUtil;

/**
 * Value Object representing the number of copies of a book.
 * @author dgarcia
 * @version 1.0
 * @date 14/01/2025
 */
public record CopiesCount(Integer value) {

    public CopiesCount {
        AssertUtil.requireNotNull(value, "Copies count cannot be null");
        AssertUtil.requireMin(value, 0, "Copies count cannot be negative");
    }

    public static CopiesCount parse(Integer value) {
        return new CopiesCount(value);
    }
}
