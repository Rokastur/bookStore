package com.dematic.bookStore.services;

import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book addNewBook(Book book) {
        return bookRepository.save(book);
    }

    public Book retrieveBookByBarcode(String barcode) throws Exception {
        if (bookRepository.existsById(barcode)) {
            return bookRepository.findById(barcode).get();
        } else throw new Exception("book with the barcode: " + barcode + " not found");
    }

    public Book updateBook(String barcode, Book updatedBook) throws Exception {
        Book existingBook = retrieveBookByBarcode(barcode);
        updateNonNullFields(updatedBook, existingBook);
        return bookRepository.save(existingBook);
    }

    public Set<String> listBarcodesForTheInStockBooksGroupedByQuantity() {
        return bookRepository.findAllBarcodesOrderByNonNullQuantityDesc();
    }

    public SortedMap<String, BigDecimal> listAllBarcodesByBookTypeAndTotalPrice(String bookType) throws Exception {
        Set<String> barcodesByBookType = bookRepository.findAllBarcodesByBookType(bookType);
        SortedMap<String, BigDecimal> barcodePriceSorted = new TreeMap<>();
        for (String s : barcodesByBookType) {
            barcodePriceSorted.put(s, calculatePriceByBarcode(s));
        }
        return barcodePriceSorted;
    }

    public BigDecimal calculatePriceByBarcode(String barcode) throws Exception {
        Book book = retrieveBookByBarcode(barcode);

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
        return nonIndexedPrice.multiply(scienceIndexAsBigDecimal).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAntiqueBookPrice(Book book, BigDecimal nonIndexedPrice) {
        LocalDate currentTime = LocalDate.now();
        var currentYear = currentTime.getYear();
        var releaseYear = ((AntiqueBook) book).getReleaseYear().getYear();
        double yearDifference = currentYear - releaseYear;
        var ageIndex = yearDifference / 10;
        return (nonIndexedPrice.multiply(new BigDecimal(ageIndex))).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateNonIndexedPrice(Book book) {
        var quantity = new BigDecimal(book.getQuantity());
        return book.getUnitPrice().multiply(quantity).setScale(2, RoundingMode.HALF_UP);

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
