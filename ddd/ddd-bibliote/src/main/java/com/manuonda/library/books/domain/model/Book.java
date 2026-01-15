package com.manuonda.library.books.domain.model;


import com.manuonda.library.books.domain.vo.BookTitle;
import com.manuonda.library.books.domain.vo.CopiesCount;
import com.manuonda.library.books.domain.vo.ISBN;

import javax.print.attribute.standard.Copies;

/**
 * Class representing a Book in the library system.
 * @author  dgarcia
 * @version 1.0
 * @date 14/01/2025
 */
public class Book {

    private BookTitle title;
    private ISBN isbn;
    private String author;
    private CopiesCount copiesCount;

    public Book() {
    }

    public Book( BookTitle title, ISBN isbn, String author, CopiesCount copiesCount) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.copiesCount = copiesCount;
    }



    public BookTitle getTitle() {
        return title;
    }

    public void setTitle(BookTitle title) {
        this.title = title;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public void setIsbn(ISBN isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public CopiesCount getCopiesCount() {
        return copiesCount;
    }

    public void setCopiesCount(CopiesCount copiesCount) {
        this.copiesCount = copiesCount;
    }

    public Boolean isAvailable() {
        return this.copiesCount.value() > 0;
    }

    public void borrowCopy(){

    }

}
