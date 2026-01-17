package com.manuonda.library.books.application;


import com.manuonda.library.books.application.dto.command.AddBookRequest;
import com.manuonda.library.books.application.dto.response.BookResponse;
import com.manuonda.library.books.domain.exception.BookNotFoundException;
import com.manuonda.library.books.domain.exception.DuplicateISBNException;
import com.manuonda.library.books.domain.model.Book;
import com.manuonda.library.books.domain.repository.BookRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

/**
 * Application Service for book use cases
 *
 */
@Service
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
            throw new DuplicateISBNException(request.isbn());
        }

        return null;
    }

    /**
     * Update an existing book in the library
     * @param isbn
     * @param request
     * @return
     */
    public BookResponse updateBook(String isbn, AddBookRequest request){
        Optional<Book> findBook = this.bookRepository.findByIsbn(isbn);
        if (!findBook.isPresent()) {
            throw new BookNotFoundException(isbn);
        }

        if(findBook.isPresent() && !findBook.get().getIsbn().isbn().equals(isbn)) {
            throw  new DuplicateISBNException(request.isbn());
        }

        Book book = findBook.get();
        book.setAuthor(request.author());
        book.setTitle(request.title());
        book.setCopiesCount(request.copies());



    }


}
