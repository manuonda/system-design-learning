package com.manuonda.library.books.domain.repository;


import com.manuonda.library.books.domain.model.Book;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface/port for managing Book entities.
 * @author dgarcia
 * @version 1.0
 * @date 14/01/2025
 */
public interface BookRepository {

    void save(Book book);
    Optional<Book> findById(Long id);
    List<Book> findAvailableBooks();
}
