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

    public Set<Author> parseAuthors(BookAuthorDTO dto) {
        if (dto.getAuthors() != null) {
            Set<Author> authors = new HashSet<>();
            for (String author : dto.getAuthors()) {
                String[] name = parseName(author);
                Optional<Author> a = authorRepository.findOneByNameAndLastName(name[0], name[1]);
                if (a.isEmpty()) {
                    a = Optional.ofNullable(saveAuthor(new Author(name[0], name[1])));
                }
                a.ifPresent(authors::add);
            }
            return authors;
        } else return new HashSet<>();
    }

    public String[] parseName(String author) {
        int spaceLocation = author.indexOf(' ');
        int length = author.length();
        String firstName = author.substring(0, spaceLocation).strip();
        String lastName = author.substring(spaceLocation, length).strip();
        return new String[]{firstName, lastName};
    }
}
