package com.elbialy.book.book;

import org.springframework.stereotype.Service;

@Service
public class Mapper {
    public BookResponse bookToBookResponse(Book book) {
        return BookResponse.builder()
                .rate(book.getRate())
                .isbn(book.getIsbn())
                .shareable(book.isShareable())
                .synopsis(book.getSynopsis())
                .title(book.getTitle())
                .authorName(book.getAuthorName())
                .archived(book.isArchived())
                .bookId(book.getId())
                .owner(book.getOwner().getFullName())
                .build();
    }
}
