package com.manuonda.library.books.domain.model;


import com.manuonda.library.books.domain.event.AddBookEvent;
import com.manuonda.library.books.domain.event.BookBorrowedEvent;
import com.manuonda.library.books.domain.event.BookReturnedCopyEvent;
import com.manuonda.library.books.domain.event.UpdatedBookEvent;
import com.manuonda.library.books.domain.exception.BookNotAvailableException;
import com.manuonda.library.books.domain.vo.Author;
import com.manuonda.library.books.domain.vo.BookTitle;
import com.manuonda.library.books.domain.vo.CopiesCount;
import com.manuonda.library.books.domain.vo.ISBN;
import com.manuonda.library.shared.AggregateRoot;


/**
 * Class representing a Book in the library system.
 * @author  dgarcia
 * @version 1.0
 * @date 14/01/2025
 */
public class Book extends AggregateRoot {

    private BookTitle title;
    private ISBN isbn;
    private Author author;
    private CopiesCount copiesCount;



    /**
     * Constructor private - only factory methods should be used to create a book
     * @param title
     * @param isbn
     * @param author
     * @param copiesCount
     */
    private Book(BookTitle title, ISBN isbn, Author author, CopiesCount copiesCount) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.copiesCount = copiesCount;
    }


    /**
     * Factory method to create a new book
     * Registers an AddBookEvent
     * @param title
     * @param isbn
     * @param author
     * @param copiesCount
     * @return
     */
    public static Book create(BookTitle title, ISBN isbn, Author author, CopiesCount copiesCount) {
       Book book = new Book(title, isbn, author, copiesCount);
       book.register(new AddBookEvent(
         isbn.isbn(),
         title.title(),
         author.author(),
         copiesCount.value()
      ));
      return book;
    }

    /**
     * Business rule : Update a book
     * @param title
     * @param isbn
     * @param author
     * @param copiesCount
     */
    public void update(BookTitle title, ISBN isbn, Author author, CopiesCount copiesCount) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.copiesCount = copiesCount;
        register(new UpdatedBookEvent(
            isbn.isbn(),
            title.title(),
            author.author(),
            copiesCount.value()
        ));
     
    }

    /**
     * Factory method to reconstitute a book from persistence
     * Does NOT register events
     * @param title
     * @param isbn
     * @param author
     * @param copiesCount
     * @return
     */
    public static Book reconstitute(BookTitle title, ISBN isbn, Author author, CopiesCount copiesCount) {
        return new Book(title, isbn, author, copiesCount);
    }
    // Business rule: a book is available if it has at least one copy
    public boolean isAvailable(){
        return this.copiesCount.value() > 0 ;
    }

    /**
     * Business rule : Borrow a copy of this book
     * Decrements the copies count by 1
     * Publishes a BookBorrowedEvent
     */
    public void borrowCopy(){
        if(!isAvailable()){
            throw new BookNotAvailableException(
                "Cannot borrow book with ISBN " + this.isbn.isbn() + " because it has no available copies"
            );
        }

        this.copiesCount = new CopiesCount(copiesCount.value() - 1);

        register(new BookBorrowedEvent(
            this.isbn.isbn(),
            this.copiesCount.value()
        ));
    }


    /**
     * Business rule : Return a copy of this book
     * Decrements the copies count by 1
     * Publishes a BookReturnedEvent
     */
    public void returnCopy(){
        this.copiesCount = new CopiesCount(this.copiesCount.value() + 1);
         register(new BookReturnedCopyEvent(
            this.isbn.isbn(),
            this.copiesCount.value()
        ));

    }

    public BookTitle getTitle() {
        return title;
    }

    public ISBN getIsbn() {
        return isbn;
    }

    public Author getAuthor() {
        return author;
    }

    public CopiesCount getCopiesCount() {
        return copiesCount;
    }
}
