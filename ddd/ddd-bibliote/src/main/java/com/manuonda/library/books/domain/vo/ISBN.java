package com.manuonda.library.books.domain.vo;


import com.manuonda.library.shared.AssertUtil;

/**
 * Value Object representing an ISBN-10 identifier.
 * @param isbn
 *
 * @author dgarcia
 * @version 1.0
 * @date 14/01/2025
 */
public record ISBN(String isbn) {

   private static final String ISBN_10_REGEX = "^(?:\\d{9}X|\\d{10})$";

   public ISBN {
       AssertUtil.requireNotBlank(isbn, "ISBN cannot be blank");
       AssertUtil.requirePattern(isbn, ISBN_10_REGEX, "ISBN must be a valid ISBN-10 format");
   }

   public static ISBN parse(String isbn){
       return new ISBN(isbn);
   }
}
