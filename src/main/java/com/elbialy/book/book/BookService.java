package com.elbialy.book.book;

import com.elbialy.book.common.PageResponse;
import com.elbialy.book.user.User;
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


@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
    private final Mapper mapper;

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

    public PageResponse<BookResponse> getByOwner(int pageNumber, int pageSize, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(pageNumber,pageSize, Sort.by("createdDate").ascending());
        Page<Book> books = bookRepository.findAllBooksByOwner(pageable,user.getId());
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
}


