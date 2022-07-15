package org.example.services;

import org.example.models.Book;
import org.example.models.Person;
import org.example.repositories.PeopleRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleService {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public List<Person> findAll(){
        return peopleRepository.findAll();
    }

    public Person findById(int id) {
        return peopleRepository.findById(id).orElse(null);
    }

    public Optional<Person> findByFullName(String fullName) {
        return Optional.ofNullable(peopleRepository.findByFullName(fullName));
    }

    public List<Book> getBooksByPersonId(int personId) {
        Optional<Person> owner = peopleRepository.findById(personId);

        if(owner.isPresent()){
            Hibernate.initialize(owner.get().getBooks());

            owner.get().getBooks().forEach(book -> {
                long diffMillSec = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                if (diffMillSec > 864000000) book.setOverdue(true);
            });

            return owner.get().getBooks();
        }
        else
            return Collections.emptyList();
    }



    @Transactional
    public void save(Person person) {
        peopleRepository.save(person);
    }

    @Transactional
    public void update(int id, Person person) {
        person.setId(id);
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }


}
