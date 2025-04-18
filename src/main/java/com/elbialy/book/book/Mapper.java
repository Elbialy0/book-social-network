package com.elbialy.book.book;

import com.elbialy.book.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

@Service
public class Mapper {
    public static BookResponse bookToBookResponse(Book book) {
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

    public static BorrowedBookResponse bookToBorrowedBookResponse(BookTransactionHistory history) {
        return BorrowedBookResponse.builder().rate(history.getBook().getRate())
                .isbn(history.getBook().getIsbn())
                .returned(history.isReturned())
                .title(history.getBook().getTitle())
                .authorName(history.getBook().getAuthorName())
                .returnedApproved(history.isReturnApproved())
                .bookId(history.getBook().getId())
                .build();
    }
}
