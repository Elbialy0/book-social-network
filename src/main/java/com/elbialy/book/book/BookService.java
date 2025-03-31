package com.elbialy.book.book;

import com.elbialy.book.user.User;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookService {
    private final BookRepository bookRepository;
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
                .orElseThrow(()-> new RuntimeException("Book not found"));
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

    public List<BookResponse> findAll() {
        return bookRepository.findAll().stream().map(book -> BookResponse.builder()
                .rate(book.getRate())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .synopsis(book.getSynopsis())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .archived(book.isArchived())
                .bookId(book.getId())
                .owner(book.getOwner().getFullName())
                .build()).collect(Collectors.toList());
    }
}
