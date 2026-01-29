package com.manuonda.library.books.interfaces;


import com.manuonda.library.books.application.dto.response.BookResponse;
import com.manuonda.library.books.domain.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class BookController {

    private final static Logger LOGGER = LoggerFactory.getLogger(BookController.class);

    @PostMapping
    @RequestMapping
    public ResponseEntity<BookResponse> createBook(@RequestBody Book book) {
      logger.
    }
}
