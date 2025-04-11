package com.elbialy.book.book;

import com.elbialy.book.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/getBooks")
    public ResponseEntity<PageResponse<BookResponse>> getBooks(@RequestParam(name = "page",defaultValue ="0")int pageNumber,
                                                               @RequestParam(name = "size",defaultValue = "10")int pageSize,
                                                               Authentication connectedUser){
        return ResponseEntity.ok(bookService.getAll(pageNumber,pageSize,connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity <PageResponse<BookResponse>> findAllBooksByOwner(
            @RequestParam(name = "page",defaultValue = "0")int pageNumber,
            @RequestParam(name = "size" , defaultValue = "10")int pageSize,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.getByOwner(pageNumber,pageSize,connectedUser));
    }


}
