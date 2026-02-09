package com.manuonda.library.books.infra.persistence;

import com.manuonda.library.shared.infra.BaseEntity;
import jakarta.persistence.*;


/**
 * Class BookEntity
 * @author dgarcia
 * @version 1.0
 * @since 1/2/2026
 */
@Entity
@Table(name="books")
public class BookEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private Integer copies;

    public BookEntity() {}

    public BookEntity(String isbn, String title, String author, Integer copies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.copies = copies;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getCopies() {
        return copies;
    }

    public void setCopies(Integer copies) {
        this.copies = copies;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
