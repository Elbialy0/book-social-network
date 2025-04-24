package com.elbialy.book.feedback;

import com.elbialy.book.book.Book;
import com.elbialy.book.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Feedback extends BaseEntity {
    private double rate;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

}
