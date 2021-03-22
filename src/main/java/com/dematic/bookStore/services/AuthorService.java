package com.dematic.bookStore.services;

import com.dematic.bookStore.entities.Author;
import com.dematic.bookStore.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public boolean exists(String firstName, String lastName) {
        return authorRepository.existsByNameAndLastName(firstName, lastName);
    }

    public Author findByFullName(String firstName, String lastName) {
        return authorRepository.getOneByNameAndLastName(firstName, lastName);
    }
}
