package com.dematic.bookStore.services;

import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import com.dematic.bookStore.services.exceptions.BookNotFoundException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
public class PriceOperations {

    private final BookRepository bookRepository;

    public PriceOperations(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BigDecimal calculatePriceByBarcode(String barcode) {
        Book book = bookRepository.findById(barcode).
                orElseThrow(() -> new BookNotFoundException("book with barcode " + barcode + " was not found"));
        var nonIndexedPrice = calculateNonIndexedPrice(book);

        if (book instanceof AntiqueBook) {
            return calculateAntiqueBookPrice(book, nonIndexedPrice);
        } else if (book instanceof ScienceJournal) {
            return calculateScienceJournalPrice(book, nonIndexedPrice);
        } else {
            return nonIndexedPrice;
        }
    }

    public BigDecimal calculateNonIndexedPrice(Book book) {
        var quantity = new BigDecimal(book.getQuantity());
        var nonRoundedTotalPrice = book.getUnitPrice().multiply(quantity);
        var roundedTotalPrice = nonRoundedTotalPrice.setScale(2, RoundingMode.HALF_UP);
        return roundedTotalPrice;
    }

    public BigDecimal calculateScienceJournalPrice(Book book, BigDecimal nonIndexedPrice) {
        var scienceIndex = new BigDecimal(((ScienceJournal) book).getScienceIndex());
        return nonIndexedPrice.multiply(scienceIndex).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateAntiqueBookPrice(Book book, BigDecimal nonIndexedPrice) {
        LocalDate currentTime = LocalDate.now();
        var currentYear = currentTime.getYear();
        var releaseYear = ((AntiqueBook) book).getReleaseYear().getYear();
        double yearDifference = currentYear - releaseYear;
        var ageIndex = yearDifference / 10;
        return (nonIndexedPrice.multiply(new BigDecimal(ageIndex))).setScale(2, RoundingMode.HALF_UP);
    }
}
