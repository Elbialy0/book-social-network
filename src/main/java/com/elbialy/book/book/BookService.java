package com.elbialy.book.book;

import com.elbialy.book.common.PageResponse;
import com.elbialy.book.exceptions.OperationNotPermittedException;
import com.elbialy.book.history.BookTransactionHistory;
import com.elbialy.book.history.BookTransactionHistoryRepository;
import com.elbialy.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;

    public Integer save(@Valid BookRequest bookRequest, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.save(Book.builder()
                .id(bookRequest.getId())
                .isbn(bookRequest.getIsbn())
                .owner(user)
                .shareable(bookRequest.isShareable())
                .synopsis(bookRequest.getSynopsis())
                .title(bookRequest.getTitle())
                .authorName(bookRequest.getAuthorName())
                .archived(false)
                .build());

        return book.getId();

    }

    public BookResponse findById(Integer bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return BookResponse.builder()
                .rate(book.getRate())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .synopsis(book.getSynopsis())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .archived(book.isArchived())
                .bookId(bookId)
                .owner(book.getOwner().getFullName())
                .build();
    }


    public PageResponse<BookResponse> getAll(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<Book> books = bookRepository.findAllBooks(pageable,user.getId());
        return pageResponse(books);

    }

    public PageResponse<BookResponse> getByOwner(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<Book> books = bookRepository.findAllBooksByOwner(pageable,user.getId());
        return pageResponse(books);

    }

    public PageResponse<BorrowedBookResponse> getBorrowed(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<BookTransactionHistory> allBorrowedBooks = bookTransactionHistoryRepository.findAllBorrowed(pageable,user.getId());

        List<BorrowedBookResponse> bookResponses = allBorrowedBooks.getContent().stream().map(Mapper::bookToBorrowedBookResponse).toList();

        return new PageResponse<>(
                bookResponses,
                allBorrowedBooks.getNumber(),
                allBorrowedBooks.getSize(),
                allBorrowedBooks.getTotalElements(),
                allBorrowedBooks.getTotalPages(),
                allBorrowedBooks.isFirst(),
                allBorrowedBooks.isLast()
        );


    }
    public static PageResponse<BookResponse> pageResponse(Page<Book> books){
        List<BookResponse> bookResponses = books.getContent().stream().map(Mapper::bookToBookResponse).toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BorrowedBookResponse> getReturned(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<BookTransactionHistory> returnedBooks = bookTransactionHistoryRepository.findAllReturned(pageable,user.getId());
        List<BorrowedBookResponse> allReturnedBooks = returnedBooks.getContent().stream().map(Mapper::bookToBorrowedBookResponse).collect(Collectors.toList());
        return new PageResponse<>(
                allReturnedBooks,
                returnedBooks.getNumber(),
                returnedBooks.getSize(),
                returnedBooks.getTotalElements(),
                returnedBooks.getTotalPages(),
                returnedBooks.isFirst(),
                returnedBooks.isLast()
        );

    }

    public Integer updateShareable(Integer bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User) authentication.getPrincipal();
        if(!Objects.equals(user.getId(),book.getOwner().getId())){

            throw new OperationNotPermittedException("You cannot update books shareable status");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;

    }

    public Integer updateArchive(Integer bookId, Authentication authentication) {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new EntityNotFoundException("Book not found"));
        User user = (User) authentication.getPrincipal();
        if(!Objects.equals(user.getId(),book.getOwner().getId())){
            throw new OperationNotPermittedException("You cannot update books archive status");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }
}


