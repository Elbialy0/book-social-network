package com.elbialy.book.history;

import com.elbialy.book.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookTransactionHistoryRepository extends JpaRepository<BookTransactionHistory, Integer> {

    @Query("""
select history
from BookTransactionHistory history
where history.returned=false
and history.user.id=:id
""")
    Page<BookTransactionHistory> findAllBorrowed(Pageable pageable, Integer id);
    @Query("""
SELECT history
FROM BookTransactionHistory history
WHERE history.user.id=:id
AND history.returned=true
AND history.returnApproved=true

""")

    Page<BookTransactionHistory> findAllReturned(Pageable pageable, Integer id);
}
