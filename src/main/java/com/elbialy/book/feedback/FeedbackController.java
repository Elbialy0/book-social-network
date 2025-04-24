package com.elbialy.book.feedback;

import com.elbialy.book.user.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<Integer> saveFeedback(FeedbackRequest feedbackRequest
    , Authentication authentication){
       return ResponseEntity.ok(feedbackService.save(feedbackRequest,(User) authentication.getPrincipal()));
    }
    @GetMapping("/owner")
    public ResponseEntity<Page<FeedbackResponse>> getAllUserFeedback(@RequestParam(name = "page",defaultValue = "0")int pageNumber,
                                                                     @RequestParam(name = "size" , defaultValue = "10")int pageSize,
                                                                     Authentication authentication){
        return ResponseEntity.ok(feedbackService.findAllUserFeedbacks(pageNumber,pageSize,(User) authentication.getPrincipal()));
    }
}
