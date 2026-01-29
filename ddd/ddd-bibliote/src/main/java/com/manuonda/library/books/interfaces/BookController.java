package com.manuonda.library.books.interfaces;


import com.manuonda.library.books.application.BookService;
import com.manuonda.library.books.application.dto.command.AddBookRequest;
import com.manuonda.library.books.application.dto.response.BookResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/")
public class BookController {

    private final BookService bookService ;
    
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> addBook(
            @RequestBody AddBookRequest request) {
        BookResponse response = bookService.addBook(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        List<BookResponse> responses = bookService.listAllBooks();
        return ResponseEntity.ok(responses);
    }
}


