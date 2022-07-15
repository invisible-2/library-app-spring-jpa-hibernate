package org.example.repositories;

import org.example.models.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BooksRepository extends JpaRepository<Book, Integer> {
    Book findByTitle(String title);
    List<Book> findByTitleStartingWith(String startingWith);
}
