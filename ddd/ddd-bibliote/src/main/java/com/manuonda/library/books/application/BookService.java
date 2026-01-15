package com.manuonda.library.books.application;


import com.manuonda.library.books.application.dto.response.BookResponse;
import com.manuonda.library.books.domain.repository.BookRepository;
import org.springframework.stereotype.Service;

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


    public BookResponse addBook()


}
