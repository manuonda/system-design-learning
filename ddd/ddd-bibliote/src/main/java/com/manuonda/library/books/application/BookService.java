package com.manuonda.library.books.application;


import com.manuonda.library.books.application.dto.command.AddBookRequest;
import com.manuonda.library.books.application.dto.response.BookResponse;
import com.manuonda.library.books.application.dto.response.ListBookResponse;
import com.manuonda.library.books.domain.exception.BookNotFoundException;
import com.manuonda.library.books.domain.exception.DuplicateISBNException;
import com.manuonda.library.books.domain.model.Book;
import com.manuonda.library.books.domain.repository.BookRepository;
import com.manuonda.library.books.domain.vo.Author;
import com.manuonda.library.books.domain.vo.BookTitle;
import com.manuonda.library.books.domain.vo.CopiesCount;
import com.manuonda.library.books.domain.vo.ISBN;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
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

    public BookService(final BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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

        Book book = new Book(
                BookTitle.parse(request.title()),
                ISBN.parse(request.isbn()),
                Author.parse(request.author()),
                CopiesCount.parse(request.copies())
        );

        this.bookRepository.save(book);
        return toResponse(book);
    }

    /**
     * Update an existing book in the library
     * @param isbn
     * @param request
     * @return
     */
    public BookResponse updateBook(String isbn, AddBookRequest request){
        Book book = this.bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));

        if(!isbn.equals(request.isbn())) {
            Optional<Book> duplicateBook = this.bookRepository.findByIsbn(isbn);
            if(duplicateBook.isPresent()) {
                throw new DuplicateISBNException(isbn);
            }
            book.setIsbn(ISBN.parse(request.isbn()));
        }

        book.setTitle(BookTitle.parse(request.title()));
        book.setAuthor(Author.parse(request.author()));
        book.setCopiesCount(CopiesCount.parse(request.copies()));

        this.bookRepository.save(book);

        return toResponse(book);

    }

    public ListBookResponse listBooks(String isbn){
       List<Book> books =  this.bookRepository.findAvailableBooks();
       List<BookResponse> bookResponses = books.stream()
               .map(this::toResponse)
               .toList();
       return bookResponses;
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
