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
        return new Book(
                BookTitle.parse(bookEntity.getTitle()),
                ISBN.parse(bookEntity.getIsbn()),
                Author.parse(bookEntity.getAuthor()),
                CopiesCount.parse(bookEntity.getCopies())
        );
    }

    // Domain -> Entity
    public static BookEntity toEntity(Book book) {
        return new BookEntity(
                book.getTitle().title(),
                book.getIsbn().isbn(),
                book.getAuthor().author(),
                book.getCopiesCount().value()
        );
    }
}
