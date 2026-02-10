package com.manuonda.library.books.infra.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Class JpaBookRepository
 * @author dgarcia
 * @version 1.0
 * @since 30/1/2026
 */
public interface JpaBookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByTitle(String title);

    Optional<BookEntity> findByIsbn(String isbn);

    @Query(
            """
            Select b From BookEntity b
            WHERE(CAST(:isbn AS string) is null or b.isbn LIKE CONCAT('%', CAST(:isbn AS string), '%'))
            """
    )
    Page<BookEntity> searchBooks(@Param("isbn") String isbn, Pageable pageable);
}