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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "API para gestión de libros")
public class BookController {

    private final BookService bookService ;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Agregar un libro", description = "Crea un nuevo libro en la biblioteca")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Libro creado exitosamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "ISBN duplicado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<BookResponse> addBook(
            @RequestBody AddBookRequest request) {
        BookResponse response = bookService.addBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar un libro", description = "Actualiza un libro existente por ISBN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "ISBN duplicado", content = @Content)
    })
    @PutMapping("/{isbn}")
    public ResponseEntity<BookResponse> updateBook(
            @PathVariable String isbn,
            @RequestBody AddBookRequest request) {
        BookResponse response = bookService.updateBook(isbn, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Prestar un libro", description = "Registra el préstamo de una copia del libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro prestado exitosamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "No hay copias disponibles", content = @Content)
    })
    @PostMapping("/{isbn}/borrow")
    public ResponseEntity<BookResponse> borrowBook(@PathVariable String isbn) {
        BookResponse response = bookService.borrowBook(isbn);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Devolver un libro", description = "Registra la devolución de una copia del libro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente",
                    content = @Content(schema = @Schema(implementation = BookResponse.class))),
            @ApiResponse(responseCode = "404", description = "Libro no encontrado", content = @Content)
    })
    @PostMapping("/{isbn}/return")
    public ResponseEntity<BookResponse> returnBook(@PathVariable String isbn) {
        BookResponse response = bookService.returnBook(isbn);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar libros", description = "Busca libros con filtro por ISBN paginado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de libros encontrados"),
            @ApiResponse(responseCode = "400", description = "Parámetros de búsqueda inválidos", content = @Content)
    })
    @GetMapping("/list")
    public ResponseEntity<PagedResult<BookResponse>> getAllBooks(
            @RequestParam(required = false) String isbn,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
            ) {
        BookFilterRequest request = new BookFilterRequest(isbn, page, size);
        PagedResult<BookResponse> responses = bookService.searchBooks(request);
        return ResponseEntity.ok(responses);
    }
}
