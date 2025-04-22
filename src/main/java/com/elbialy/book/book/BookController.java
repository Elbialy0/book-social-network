package com.elbialy.book.book;

import com.elbialy.book.common.PageResponse;
import com.elbialy.book.user.User;
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
    public ResponseEntity<BookResponse> getBook(@PathVariable(name ="book-id") Integer bookId) {
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
    @GetMapping("/borrowed")
    public ResponseEntity <PageResponse<BorrowedBookResponse>> findAllBorrowedBooks(
            @RequestParam(name = "page",defaultValue = "0")int pageNumber,
            @RequestParam(name = "size" , defaultValue = "10")int pageSize,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.getBorrowed(pageNumber,pageSize,connectedUser));
    }
    @GetMapping("/returned")
    public ResponseEntity <PageResponse<BorrowedBookResponse>> findAllReturnedBooks(
            @RequestParam(name = "page",defaultValue = "0")int pageNumber,
            @RequestParam(name = "size" , defaultValue = "10")int pageSize,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(bookService.getReturned(pageNumber,pageSize,connectedUser));
    }

    @PatchMapping("/shareable/{book-id}")
    public ResponseEntity<Integer> updateShareableStatus(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication authentication
    ){
        return ResponseEntity.ok(bookService.updateShareable(bookId,authentication));
    }
    @PatchMapping("/archive/{book-id}")
    public ResponseEntity<Integer> updateArchiveStatus(
            @PathVariable(name = "book-id") Integer bookId,
            Authentication authentication
    ){
        return ResponseEntity.ok(bookService.updateArchive(bookId,authentication));
    }
    @PostMapping("/borrow/{book-id}")
    public ResponseEntity<Integer> borrowBook(@PathVariable(name = "book-id")int bookId, Authentication authentication){
        return ResponseEntity.ok(bookService.borrowBook(bookId,(User) authentication.getPrincipal()));
    }
    @PatchMapping("/borrow/returned/{book-id}")
    public ResponseEntity<Integer> borrowBookReturned(@PathVariable(name = "book-id")int bookId, Authentication authentication){
        return ResponseEntity.ok(bookService.returnedBook(bookId,(User) authentication.getPrincipal()));
    }



}
