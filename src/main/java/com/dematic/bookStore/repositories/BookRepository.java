package com.dematic.bookStore.repositories;

import com.dematic.bookStore.entities.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface BookRepository extends CrudRepository<Book, String> {

    @Query(value = "SELECT Barcode FROM BOOK WHERE quantity > 0 ORDER BY COUNT DESC", nativeQuery = true)
    Set<String> findAllBarcodesOrderByNonNullQuantityDesc();
}