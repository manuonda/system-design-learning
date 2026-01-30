package com.manuonda.library.books.infra.persistence;

import com.manuonda.library.books.domain.dto.BookSearchCriteria;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Class JPABookRepsitory
 * @author dgarcia
 * @version 1.0
 * @since 30/1/2026
 */
public interface JpaBookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByTitle(String title);

    @Query("SELECT b FROM BookEntity b WHERE b.copies > 0 ")
    List<BookEntity> findAllCopies();

    Optional<BookEntity> findByIsbn(String isbn);

    @Query(
            """
            Select b From BookEntity b
            WHERE(:isbn is null or b.isbn LIKE CONCAT('%',:isbn, '%'))
            """
    )
    Page<BookEntity> searchBooks(@Param("isbn") String isbn, Pageable pageable);
}