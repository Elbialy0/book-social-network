package com.elbialy.book.feedback;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class FeedbackResponse {
    String comment;
    String BookTitle;
    double rate;
}
