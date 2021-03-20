package com.dematic.bookStore.services;

import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addNewBook(Book book) {
        return bookRepository.save(book);
    }

    public Optional<Book> retrieveBookByBarcode(String barcode) {
        return bookRepository.findById(barcode);
    }

    public Book updateBook(String barcode, Book updatedBook) throws Exception {
        Book existingBook;
        if (retrieveBookByBarcode(barcode).isPresent()) {
            existingBook = retrieveBookByBarcode(barcode).get();
        } else throw new Exception("book with the barcode: " + barcode + " not found");
        updateNonNullFields(updatedBook, existingBook);
        return bookRepository.save(existingBook);
    }

    public Set<String> listBarcodesForTheInStockBooksGroupedByQuantity() {
        return bookRepository.findAllBarcodesOrderByNonNullQuantityDesc();
    }

    public BigDecimal calculatePriceByBarcode(String barcode) throws Exception {
        Book book;
        if (retrieveBookByBarcode(barcode).isPresent()) {
            book = retrieveBookByBarcode(barcode).get();
        } else throw new Exception("book with the barcode: " + barcode + " not found");

        var nonIndexedPrice = calculateNonIndexedPrice(book);

        if (book instanceof AntiqueBook) {
            return calculateAntiqueBookPrice(book, nonIndexedPrice);
        } else if (book instanceof ScienceJournal) {
            return calculateScienceJournalPrice(book, nonIndexedPrice);
        } else {
            return nonIndexedPrice;
        }
    }

    public BigDecimal calculateScienceJournalPrice(Book book, BigDecimal nonIndexedPrice) {
        var scienceIndexAsBigDecimal = new BigDecimal(((ScienceJournal) book).getScienceIndex());
        return nonIndexedPrice.multiply(scienceIndexAsBigDecimal);
    }

    public BigDecimal calculateAntiqueBookPrice(Book book, BigDecimal nonIndexedPrice) {
        LocalDate currentTime = LocalDate.now();
        var currentYear = currentTime.getYear();
        var releaseYear = ((AntiqueBook) book).getReleaseYear().getYear();
        var ageIndex = (currentYear - releaseYear) / 10;
        return nonIndexedPrice.multiply(new BigDecimal(ageIndex));
    }

    public BigDecimal calculateNonIndexedPrice(Book book) {
        var quantity = new BigDecimal(book.getQuantity());
        return book.getUnitPrice().multiply(quantity);

    }

    public void updateNonNullFields(Book updatedBook, Book existingBook) {
        if (updatedBook.getBarcode() != null) {
            existingBook.setBarcode(updatedBook.getBarcode());
        }
        if (!updatedBook.getAuthors().isEmpty()) {
            existingBook.setAuthors(updatedBook.getAuthors());
        }
        if (updatedBook.getQuantity() != null) {
            existingBook.setQuantity(updatedBook.getQuantity());
        }
        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getUnitPrice() != null) {
            existingBook.setUnitPrice(updatedBook.getUnitPrice());
        }
    }
}