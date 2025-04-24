package com.elbialy.book.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    @Query("""
SELECT Feedback
FROM Feedback f
WHERE f.book.id=:id
""")
    Page<Feedback> findAllBookFeedbacks(Pageable pageable, Integer id);
}
