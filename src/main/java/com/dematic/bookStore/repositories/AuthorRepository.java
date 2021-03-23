package com.dematic.bookStore.repositories;

import com.dematic.bookStore.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    boolean existsByNameAndLastName(String name, String lastName);

    Author getOneByNameAndLastName(String name, String lastName);

    Optional<Author> findOneByNameAndLastName(String name, String lastName);
}
