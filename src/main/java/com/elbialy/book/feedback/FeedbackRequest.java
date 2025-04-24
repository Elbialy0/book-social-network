package com.elbialy.book.feedback;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.*;
import lombok.Getter;
import org.aspectj.bridge.IMessage;

public record FeedbackRequest(
        @Positive(message = "200")
        @Min(value = 0 , message ="201")
        @Max(value = 5, message = "202")
        Integer note,
        @NotNull(message = "203")
        @NotEmpty(message = "203")
        @NotBlank(message = "203")
        String comment,
        @NotNull(message = "204")
        @NotBlank(message = "204")
        Integer bookId



) {
}
