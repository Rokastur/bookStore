package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.utility.BookAuthorDTO;
import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Author;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookUpdateOperationsTest {


    static BookRepository bookRepository;
    static BookUpdateOperations operations;


    static Set<Author> authors = new HashSet<>();
    static Author a = new Author();
    static Book book;
    static BookAuthorDTO dto;


    @BeforeAll
    static void setUp() {
        bookRepository = Mockito.mock(BookRepository.class);
        operations = new BookUpdateOperations(bookRepository);
        a.setName("test name");
        a.setLastName("test lastName");
        authors.add(a);
        book = new Book("0123456789", "test book", 5, new BigDecimal("10"), authors);
        dto = new BookAuthorDTO();
        dto.setBarcode("0123456789");
        dto.setQuantity(1);
        dto.setTitle("dto");
        dto.setUnitPrice(new BigDecimal("5"));
        dto.setScienceIndex(5);
        dto.setReleaseYear(LocalDate.now());
    }

    @Test
    void updateScienceIndexOrConvertBookType() {
        //when passing ScienceJournal and science index is not null,
        // update science index and return instance of ScienceJournal
        dto.setScienceIndex(5);
        ScienceJournal journal = new ScienceJournal(book, 1);
        assertTrue(operations.updateScienceIndexOrConvertBookType(journal, dto) instanceof ScienceJournal);
        assertEquals(((ScienceJournal) operations.updateScienceIndexOrConvertBookType(journal, dto)).getScienceIndex(),
                dto.getScienceIndex());

        //when passing Book and dto contains science index, return instance of ScienceJournal with
        //science index set
        assertTrue(operations.updateScienceIndexOrConvertBookType(book, dto) instanceof ScienceJournal);
        assertEquals(((ScienceJournal) operations.updateScienceIndexOrConvertBookType(book, dto)).getScienceIndex(),
                dto.getScienceIndex());

    }

    @Test
    void updateReleaseYearOrConvertBookType() {
        //when passing ScienceJournal and date within threshold, return instance of AntiqueBook
        dto.setReleaseYear(LocalDate.of(1850, Month.JANUARY, 1));
        AntiqueBook antiqueBook = new AntiqueBook(book, dto.getReleaseYear());
        AntiqueBook a = (AntiqueBook) operations.updateReleaseYearOrConvertBookType(antiqueBook, dto);
        assertTrue(operations.updateReleaseYearOrConvertBookType(antiqueBook, dto) instanceof AntiqueBook);
        assertEquals(dto.getReleaseYear(), a.getReleaseYear());

        //when passing ScienceJournal and date not within the threshold, returned instance is not AntiqueBook
        dto.setReleaseYear(LocalDate.now());
        assertFalse(operations.updateReleaseYearOrConvertBookType(antiqueBook, dto) instanceof AntiqueBook);

        //when passing Book and date is within threshold, convert to AntiqueBook
        dto.setReleaseYear(LocalDate.of(1850, Month.JANUARY, 1));
        assertTrue(operations.updateReleaseYearOrConvertBookType(book, dto) instanceof AntiqueBook);
    }

    @Test
    void transformToAntiqueBook() {
        doNothing().when(bookRepository).deleteById("0123456789");
        AntiqueBook antiqueBook = operations.transformToAntiqueBook(book, LocalDate.of(1850, Month.JANUARY, 1));
        assertNotNull(antiqueBook);
        assertNotNull(antiqueBook.getReleaseYear());
    }

    @Test
    void transformToScienceJournal() {
        doNothing().when(bookRepository).deleteById("0123456789");
        ScienceJournal scienceJournal = operations.transformToScienceJournal(book, 5);
        assertNotNull(scienceJournal);
        assertNotNull(scienceJournal.getScienceIndex());

    }
}