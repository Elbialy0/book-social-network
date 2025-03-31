package com.elbialy.book.book;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book")

public class BookController {
    private final BookService bookService;

    @PostMapping("/save")
    public ResponseEntity<Integer> saveBook(@RequestBody @Valid BookRequest bookRequest,
                                            Authentication authentication) {
        return ResponseEntity.ok(bookService.save(bookRequest,authentication));

    }
    @GetMapping("{book-id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Integer bookId) {
        return ResponseEntity.ok(bookService.findById(bookId));

    }
}
