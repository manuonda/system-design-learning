package com.manuonda.library.books.interfaces;


import com.manuonda.library.books.application.BookService;
import com.manuonda.library.books.application.dto.command.AddBookRequest;
import com.manuonda.library.books.application.dto.command.BookFilterRequest;
import com.manuonda.library.books.application.dto.response.BookResponse;
import com.manuonda.library.shared.PagedResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@Tag(name = "Books", description = "API para gestión de libros")
public class BookController {

    private final BookService bookService ;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Agregar un libro", description = "Crea un nuevo libro en la biblioteca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro creado exitosamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "ISBN duplicado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BookResponse> addBook(
            @RequestBody AddBookRequest request) {
        BookResponse response = bookService.addBook(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar libros", description = "Busca libros con filtro por ISBN paginado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de libros encontrados"),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos", content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<PagedResult<BookResponse>> getAllBooks(
            @RequestBody BookFilterRequest request
            ) {
        PagedResult<BookResponse> responses = bookService.searchBooks(request);
        return ResponseEntity.ok(responses);
    }
}
