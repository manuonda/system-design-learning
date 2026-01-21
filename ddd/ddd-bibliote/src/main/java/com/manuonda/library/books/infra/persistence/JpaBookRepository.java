package com.manuonda.library.books.infra.persistence;

import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaBookRepository extends JpaRepository<BookEntity, Long> {
    Optional<BookEntity> findByTitle(String title);

    @Query("SELECT b FROM BookEntity b WHERE b.copies > 0 ")
    List<BookEntity> findAllCopies();
}
