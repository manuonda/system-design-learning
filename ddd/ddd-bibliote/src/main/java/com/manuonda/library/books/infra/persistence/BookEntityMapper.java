package com.manuonda.library.books.infra.persistence;

import com.manuonda.library.books.domain.model.Book;
import com.manuonda.library.books.domain.vo.Author;
import com.manuonda.library.books.domain.vo.BookTitle;
import com.manuonda.library.books.domain.vo.CopiesCount;
import com.manuonda.library.books.domain.vo.ISBN;

/**
 * Class Util Mapper
 */
public class BookEntityMapper {
    private BookEntityMapper() {}

    // Entity  -> Domain
    public static Book toDomain(BookEntity bookEntity) {
        return Book.reconstitute(
                BookTitle.parse(bookEntity.getTitle()),
                ISBN.parse(bookEntity.getIsbn()),
                Author.parse(bookEntity.getAuthor()),
                CopiesCount.parse(bookEntity.getCopies())
        );
    }

    // Domain -> Entity (new)
    public static BookEntity toEntity(Book book) {
        return new BookEntity(
                book.getIsbn().isbn(),
                book.getTitle().title(),
                book.getAuthor().author(),
                book.getCopiesCount().value()
        );
    }

    // Domain -> Entity (existing)
    public static void updateEntity(BookEntity entity, Book book) {
        entity.setIsbn(book.getIsbn().isbn());
        entity.setTitle(book.getTitle().title());
        entity.setAuthor(book.getAuthor().author());
        entity.setCopies(book.getCopiesCount().value());
    }
}
