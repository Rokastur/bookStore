package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.BookAuthorDTO;
import com.dematic.bookStore.entities.Author;
import com.dematic.bookStore.repositories.AuthorRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Set<Author> retrieveOrCreateAuthorsFromDB(Set<BookAuthorDTO.AuthorDTO> authorDTOs) {
        Set<Author> authors = new HashSet<>();
        for (BookAuthorDTO.AuthorDTO authorDTO : authorDTOs) {
            var fName = authorDTO.getName();
            var lName = authorDTO.getLastName();
            Optional<Author> a = authorRepository.findOneByNameAndLastName(authorDTO.getName(), authorDTO.getLastName());
            if (a.isEmpty()) {
                a = Optional.ofNullable(saveAuthor(new Author(fName, lName)));
            }
            a.ifPresent(authors::add);
        }
        return authors;
    }
}
