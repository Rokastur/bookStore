package com.dematic.bookStore.repositories;

import com.dematic.bookStore.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorRepository extends JpaRepository<Author, Long> {

    Optional<Author> findOneByNameAndLastName(String name, String lastName);
}
