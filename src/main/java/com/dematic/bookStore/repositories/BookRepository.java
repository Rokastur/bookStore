package com.dematic.bookStore.repositories;

import com.dematic.bookStore.entities.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface BookRepository extends CrudRepository<Book, String> {

    @Query(value = "SELECT Barcode FROM BOOK WHERE quantity > 0 ORDER BY QUANTITY DESC", nativeQuery = true)
    List<String> findAllNonNullBarcodesOrderByQuantityDesc();

    @Query(value = "SELECT Barcode FROM BOOK WHERE book_type =:bookType", nativeQuery = true)
    Set<String> findAllBarcodesByBookType(String bookType);
}
