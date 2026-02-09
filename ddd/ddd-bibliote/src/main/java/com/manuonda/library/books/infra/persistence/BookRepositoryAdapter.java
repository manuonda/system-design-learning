package com.manuonda.library.books.infra.persistence;


import com.manuonda.library.books.domain.dto.BookSearchCriteria;
import com.manuonda.library.books.domain.model.Book;
import com.manuonda.library.books.domain.repository.BookRepository;
import com.manuonda.library.shared.PagedResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Class BookRepository Adapter
 * @author dgarcia
 * @version 1.0
 * @date 24/01/2026
 */


@Repository
public class BookRepositoryAdapter implements BookRepository {

    private static final String ISBN = "isbn";
    private final JpaBookRepository jpaBookRepository;

    public BookRepositoryAdapter(JpaBookRepository jpaBookRepository) {
       this.jpaBookRepository = jpaBookRepository;
    }


    @Override
    public void create(Book book) {
        BookEntity entity = BookEntityMapper.toEntity(book);
        jpaBookRepository.save(entity);
    }

    @Override
    public void update(Book book) {
        BookEntity entity = jpaBookRepository.findByIsbn(book.getIsbn().isbn())
                .orElseThrow();
        BookEntityMapper.updateEntity(entity, book);
        jpaBookRepository.save(entity);
    }


    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return this.jpaBookRepository.findByIsbn(isbn)
                .map(BookEntityMapper::toDomain);
    }

    @Override
    public PagedResult<Book> searchBooks(BookSearchCriteria request) {
        Sort sort = Sort.by(Sort.Direction.ASC, ISBN);
        PageRequest pageRequest = PageRequest.of(request.page() -1, request.size(),sort);
        Page<BookEntity> booksEntity = this.jpaBookRepository.searchBooks(request.isbn(),pageRequest);
        Page<Book> domainPage = booksEntity.map(BookEntityMapper::toDomain);
        return new PagedResult<>(
                domainPage.getContent(),
                domainPage.getTotalElements(),
                domainPage.getNumber() + 1,
                domainPage.getTotalPages(),
                domainPage.isFirst(),
                domainPage.isLast(),
                domainPage.hasNext(),
                domainPage.hasPrevious()
        );
    }
}
