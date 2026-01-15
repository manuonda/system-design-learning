package com.manuonda.library.books.domain.vo;

import com.manuonda.library.shared.AssertUtil;

public record Author(String author) {

    public Author{
        AssertUtil.requireNotNull(author, "Author cannot be null");
        AssertUtil.requireNotBlank(author, "Author cannot be blank");
    }

    public static Author parse(String author){
        return new Author(author);
    }
}
