package com.dematic.bookStore.services;

import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class PriceOperationsTest {

    PriceOperations operations = new PriceOperations(null);

    static Book book = new Book();
    static ScienceJournal scienceJournal = new ScienceJournal();
    static AntiqueBook antiqueBook = new AntiqueBook();
    static BigDecimal unitPrice;

    @BeforeAll
    static void setUp() {
        unitPrice = new BigDecimal("5.525");
    }

    @Test
    void calculateNonIndexedPrice() {
        book.setQuantity(13);
        book.setUnitPrice(unitPrice);
        assertEquals(new BigDecimal("71.83"), operations.calculateNonIndexedPrice(book));
    }

    @Test
    void calculateScienceJournalPrice() {
        scienceJournal.setQuantity(13);
        scienceJournal.setUnitPrice(unitPrice);
        scienceJournal.setScienceIndex(4);
        assertEquals(new BigDecimal("287.32"), operations.calculateScienceJournalPrice(scienceJournal, new BigDecimal("71.83")));
    }

    @Test
    void calculateAntiqueBookPrice() {
        antiqueBook.setQuantity(13);
        antiqueBook.setUnitPrice(unitPrice);
        antiqueBook.setReleaseYear(LocalDate.of(1850, Month.JANUARY, 1));
        assertEquals(new BigDecimal("1228.29"), operations.calculateAntiqueBookPrice(antiqueBook, new BigDecimal("71.83")));
    }
}