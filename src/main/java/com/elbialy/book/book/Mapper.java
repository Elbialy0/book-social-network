package com.elbialy.book.book;

import com.elbialy.book.history.BookTransactionHistory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class Mapper {
    public static BookResponse bookToBookResponse(Book book) throws IOException {
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
                .cover(getUri(book))
                .build();
    }

    private static String getUri(Book book) {
        return "http://localhost:8080/api/v1/books/"+book.getId()+"/cover";
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
