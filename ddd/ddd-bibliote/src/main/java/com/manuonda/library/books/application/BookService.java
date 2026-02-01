package com.manuonda.library.books.application;


import com.manuonda.library.books.application.dto.command.AddBookRequest;
import com.manuonda.library.books.application.dto.command.BookFilterRequest;
import com.manuonda.library.books.application.dto.response.BookResponse;
import com.manuonda.library.books.domain.dto.BookSearchCriteria;
import com.manuonda.library.books.domain.exception.BookNotFoundException;
import com.manuonda.library.books.domain.exception.DuplicateISBNException;
import com.manuonda.library.books.domain.model.Book;
import com.manuonda.library.books.domain.repository.BookRepository;
import com.manuonda.library.books.domain.vo.Author;
import com.manuonda.library.books.domain.vo.BookTitle;
import com.manuonda.library.books.domain.vo.CopiesCount;
import com.manuonda.library.books.domain.vo.ISBN;
import com.manuonda.library.shared.DomainEventPublisher;
import com.manuonda.library.shared.PagedResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Application Service for book use cases
 * @author dgarcia
 * @version 1.0
 * @date    20/1/2026
 *
 */
@Service
@Transactional
public class BookService {

    private final BookRepository bookRepository;
    private final DomainEventPublisher domainEventPublisher;

    public BookService(final BookRepository bookRepository, 
        DomainEventPublisher domainEventPublisher) {
        this.bookRepository = bookRepository;
        this.domainEventPublisher = domainEventPublisher;
    }


    /**
     * Add a new book to the library
     * @param request
     * @return
     */
    public BookResponse addBook(AddBookRequest request){
        Optional<Book> findBook = this.bookRepository.findByIsbn(request.isbn());
        if (findBook.isPresent()) {
            throw new DuplicateISBNException("ISBN already exists :"  + request.isbn());
        }

        Book book = Book.create(
                BookTitle.parse(request.title()),
                ISBN.parse(request.isbn()),
                Author.parse(request.author()),
                CopiesCount.parse(request.copies())
        );

        Book bookSaved = this.bookRepository.save(book);
        this.domainEventPublisher.publish(bookSaved.pullDomainEvents());
        return toResponse(book);
    }

    /**
     * Update an existing book in the library
     * @param isbn
     * @param request
     * @return
     */
    public BookResponse updateBook(String isbn, AddBookRequest request){
        //Load the book to update
        Book book = this.bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        //Check if the ISBN is different
        if(!isbn.equals(request.isbn())) {
            //Check if the new ISBN is already in use
            Optional<Book> duplicateBook = this.bookRepository.findByIsbn(request.isbn());
            if(duplicateBook.isPresent()) {
                throw new DuplicateISBNException(isbn);
            }
        }

        book.update(
                BookTitle.parse(request.title()),
                ISBN.parse(request.isbn()),
                Author.parse(request.author()),
                CopiesCount.parse(request.copies())
        );

        this.bookRepository.save(book);
        this.domainEventPublisher.publish(book.pullDomainEvents());
        return toResponse(book);

    }


    @Transactional(readOnly = true)
    public PagedResult<BookResponse> searchBooks(
            BookFilterRequest bookFilterRequest
    ){
        BookSearchCriteria bookSearchCriteria = new BookSearchCriteria(
                bookFilterRequest.isbn(),
                bookFilterRequest.page(),
                bookFilterRequest.size()
        );
        PagedResult<Book> books = this.bookRepository.searchBooks(bookSearchCriteria);
        return PagedResult.of(books, this::toResponse);
    }

    /**
     * Mapper convert entity to domain dto of request
     * @param book
     * @return
     */
    private BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getIsbn().isbn(),
                book.getTitle().title(),
                book.getAuthor().author(),
                book.getCopiesCount().value()
        );
    }



}
