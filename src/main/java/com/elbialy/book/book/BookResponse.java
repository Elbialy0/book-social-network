package com.elbialy.book.book;

import lombok.*;
import org.springframework.core.io.UrlResource;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {
    private Integer bookId;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;
    private String  cover;
    private double rate;
    private boolean archived;
    private boolean shareable;
}
