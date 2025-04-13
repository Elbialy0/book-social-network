package com.elbialy.book.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query(""" 
SELECT book
FROM Book book
where book.archived = false and book.shareable= true
and book.owner.id != :id
""")
    Page<Book> findAllBooks(Pageable pageable, Integer id);
    @Query("""
select book
from Book book
where book.owner.id = :id
""")

    Page<Book> findAllBooksByOwner(Pageable pageable, Integer id);
}
