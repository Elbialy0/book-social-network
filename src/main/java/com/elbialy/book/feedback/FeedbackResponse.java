package com.elbialy.book.feedback;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackResponse {
    String comment;
    String BookTitle;
    double rate;
}
