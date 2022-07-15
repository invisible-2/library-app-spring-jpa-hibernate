package org.example.services;

import org.example.models.Book;
import org.example.models.Person;
import org.example.repositories.BooksRepository;
import org.example.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BooksService {

    private final BooksRepository booksRepository;

    @Autowired
    public BooksService(BooksRepository booksRepository) {
        this.booksRepository = booksRepository;
    }

    public List<Book> findAll() {
        return booksRepository.findAll();
    }

    public Book findById(int id) {
        return booksRepository.findById(id).orElse(null);
    }

    public Optional<Book> findByTitle(String title) {
        return Optional.ofNullable(booksRepository.findByTitle(title));
    }

    public Person getOwnerByBookId(int bookId) {
        Optional<Book> book = booksRepository.findById(bookId);

        if(book.isPresent()){
            Hibernate.initialize(book.get().getOwner());
            return book.get().getOwner();
        }
        else
            return null;
    }

    public List<Book> findBooksByTitleStartingWith(String startingWith) {
        return booksRepository.findByTitleStartingWith(startingWith);
    }

    public List<Book> findByPages(Integer page, Integer itemsPerPage, boolean sortByYear) {
        if(sortByYear)
            return  booksRepository.findAll(PageRequest.of(page, itemsPerPage, Sort.by("year"))).getContent();
        else
            return booksRepository.findAll(PageRequest.of(page, itemsPerPage)).getContent();
    }

    @Transactional
    public void save(Book book) {
        book.setTakenAt(new Date());
        booksRepository.save(book);
    }

    @Transactional
    public void update(int id, Book book) {
        book.setBookId(id);
        booksRepository.save(book);
    }

    @Transactional
    public void delete(int id) {
        booksRepository.deleteById(id);
    }

    @Transactional
    public void giveOrCancelBook(int bookId, Person person) {
        Optional<Book> book = booksRepository.findById(bookId);
        book.get().setOwner(person);
    }

}
