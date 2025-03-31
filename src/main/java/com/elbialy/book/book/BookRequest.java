package com.elbialy.book.book;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookRequest {
    Integer id;
    @NotNull(message = "100")
     @NotEmpty(message = "100")
    String title;
    @NotNull(message = "101")
    @NotEmpty(message = "101")
    String authorName;
    @NotNull(message = "102")
    @NotEmpty(message = "102")
    String isbn;
    @NotNull(message = "103")
    @NotEmpty(message = "103")
    String synopsis;
    boolean shareable;

}
