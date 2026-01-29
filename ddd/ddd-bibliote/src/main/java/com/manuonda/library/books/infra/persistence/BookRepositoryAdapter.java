package com.manuonda.library.books.infra.persistence;


import com.manuonda.library.books.domain.model.Book;
import com.manuonda.library.books.domain.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Class BookRepository Adapter
 * @author dgarcia
 * @version 1.0
 * @date 24/01/2026
 */


@Repository
public class BookRepositoryAdapter implements BookRepository {

    private final JpaBookRepository jpaBookRepository;

    public BookRepositoryAdapter(JpaBookRepository jpaBookRepository) {

        this.jpaBookRepository = jpaBookRepository;
    }


    @Override
    public void save(Book book) {
     Optional<BookEntity> bookExisting = this.jpaBookRepository.findByIsbn(book.getIsbn().isbn());
     if (bookExisting.isPresent()) {
         jpaBookRepository.save(bookExisting.get());
     } else {
         BookEntity entity = BookEntityMapper.toEntity(book);
         jpaBookRepository.save(entity);
     }

    }

    @Override
    public Optional<Book> findById(Long id) {
        return this.jpaBookRepository.findById(id)
                .map(BookEntityMapper::toDomain);
    }

    @Override
    public List<Book> findAvailableBooks() {
        return jpaBookRepository.findAllCopies()
                .stream()
                .map(BookEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return this.jpaBookRepository.findByIsbn(isbn)
                .map(BookEntityMapper::toDomain);
    }
}
