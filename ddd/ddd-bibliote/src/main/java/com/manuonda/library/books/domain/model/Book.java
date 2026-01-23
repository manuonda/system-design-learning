package com.manuonda.library.books.domain.model;


import com.manuonda.library.books.domain.vo.Author;
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
    private Author author;
    private CopiesCount copiesCount;

    public Book() {
    }

    public Book( BookTitle title, ISBN isbn, Author author, CopiesCount copiesCount) {
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
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
