package com.elbialy.book.feedback;

import com.elbialy.book.book.Book;
import com.elbialy.book.book.BookRepository;
import com.elbialy.book.book.BookResponse;
import com.elbialy.book.common.PageResponse;
import com.elbialy.book.exceptions.OperationNotPermittedException;
import com.elbialy.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;
    private final BookRepository bookRepository;
    public Integer save(FeedbackRequest feedbackRequest, User principal) {
        Book book = bookRepository.findById(feedbackRequest.bookId()).orElseThrow(()->new EntityNotFoundException("Book not found"));

        if(Objects.equals(principal.getId(), book.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannot give feedback to your own book");
        }
        if(book.isArchived()||!book.isShareable()){
            throw new OperationNotPermittedException("You cannot give feedback to archived or non-shareable book");
        }
         Feedback feedback = feedbackRepository.save(Feedback.builder().rate(feedbackRequest.note())
                .book(book)
                .comment(feedbackRequest.comment())
                        .build()
                );
        return feedback.getId();
    }

    public PageResponse<FeedbackResponse> findAllBookFeedbacks(int pageNumber, int pageSize, User principal,int id) {
        Pageable pageable = PageRequest.of(pageNumber,pageSize);
        Page<Feedback> page = feedbackRepository.findAllBookFeedbacks(pageable,id);
        List<FeedbackResponse> feedbackResponses = page.getContent().stream().map((feedback )->
                FeedbackResponse.builder()
                        .BookTitle(feedback.getBook().getTitle())
                        .comment(feedback.getComment())
                        .rate(feedback.getRate())
                        .build()).toList();
        return new PageResponse<>(
               feedbackResponses ,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );

    }
}
