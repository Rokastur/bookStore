package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.utility.BarcodesWrapper;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    static BookService bookService;
    static BookRepository bookRepository;
    static PriceOperations priceOperations;

    static BookAuthorDTO dto = new BookAuthorDTO();
    static Set<Author> authors = new HashSet<>();
    static Set<BookAuthorDTO.AuthorDTO> authorDTOs = new HashSet<>();

    static Book book = new Book();
    static ScienceJournal journal;
    static AntiqueBook antique;

    static Author author1;
    static Author author2;

    @BeforeAll
    static void setUp() {

        priceOperations = Mockito.mock(PriceOperations.class);
        bookRepository = Mockito.mock(BookRepository.class);

        bookService = new BookService(bookRepository, null, priceOperations, null);

        author1 = new Author("John", "Doe");
        author1.setId(1L);
        author2 = new Author("Jane", "Doe");
        author2.setId(2L);

        authors.add(author1);

        BookAuthorDTO.AuthorDTO authorDTO = new BookAuthorDTO.AuthorDTO();
        authorDTO.setName("dtoName");
        authorDTO.setLastName("dtoLastName");

        authorDTOs.add(authorDTO);

        dto.setBarcode("9780596520687");
        dto.setQuantity(10);
        dto.setTitle("my dto");
        dto.setUnitPrice(new BigDecimal("5.5"));

        book.setBarcode("4564684381031");
        book.setQuantity(2);
        book.setTitle("my book");
        book.setUnitPrice(new BigDecimal("10"));
        book.addAuthors(authors);

        journal = new ScienceJournal(book, 4);
        LocalDate release = LocalDate.of(1890, Month.JANUARY, 1);
        antique = new AntiqueBook(book, release);
    }


    @Test
    void bookIsScienceJournal() {
        assertFalse(bookService.bookIsScienceJournal(dto));
        dto.setScienceIndex(4);
        assertTrue(bookService.bookIsScienceJournal(dto));
    }

    @Test
    void bookIsAntique() {
        assertFalse(bookService.bookIsAntique(dto));
        LocalDate releaseYear = LocalDate.of(1850, Month.JANUARY, 1);
        dto.setReleaseYear(releaseYear);
        assertTrue(bookService.bookIsAntique(dto));
        dto.setReleaseYear(LocalDate.of(1901, Month.JANUARY, 1));
        assertFalse(bookService.bookIsAntique(dto));
    }

    @Test
    void createNewScienceJournal() {
        ScienceJournal journal = new ScienceJournal(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors, dto.getScienceIndex());
        assertEquals(journal, bookService.createNewScienceJournal(dto, authors));
    }

    @Test
    void createNewAntiqueBook() {
        AntiqueBook antique = new AntiqueBook(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors, dto.getReleaseYear());
        assertEquals(antique, bookService.createNewAntiqueBook(dto, authors));
    }

    @Test
    void createNewRegularBook() {
        Book book = new Book(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors);
        assertEquals(book, bookService.createNewRegularBook(dto, authors));
    }

    @Test
    void getBarcodesWrapperForTheBooksInStock() {
        List<String> barcodesInStock = new ArrayList<>();
        barcodesInStock.add("ABC");
        barcodesInStock.add("DEF");
        barcodesInStock.add("GHI");

        when(bookRepository.findAllNonNullBarcodesOrderByQuantityDesc()).thenReturn(barcodesInStock);

        List<BarcodesWrapper> wrapper = bookService.getBarcodesWrapperForTheBooksInStock();
        assertEquals(wrapper.size(), 3);
        assertEquals(wrapper.get(0).getBarcode(), "ABC");
        assertEquals(wrapper.get(1).getBarcode(), "DEF");
        assertEquals(wrapper.get(2).getBarcode(), "GHI");
    }

    @Test
    void calculateAndSortPriceForBarcodesOfBookType() {

        Set<String> toSort = new HashSet<>();
        toSort.add("GHI");
        toSort.add("ABC");
        toSort.add("DEF");

        when(bookRepository.findAllBarcodesByBookType("Book")).thenReturn(toSort);

        when(priceOperations.calculatePriceByBarcode("ABC")).thenReturn(new BigDecimal("10"));
        when(priceOperations.calculatePriceByBarcode("GHI")).thenReturn(new BigDecimal("15"));
        when(priceOperations.calculatePriceByBarcode("DEF")).thenReturn(new BigDecimal("5"));

        BookService bookService = new BookService(bookRepository, null, priceOperations, null);

        List<String> sorted = bookService.calculateAndSortPriceForBarcodesOfBookType("Book");
        assertEquals(sorted.get(0), "DEF/5");
        assertEquals(sorted.get(1), "ABC/10");
        assertEquals(sorted.get(2), "GHI/15");
    }

    @Test
    void extractBarcodesToBarcodesWrapper() {
        List<String> sorted = new ArrayList<>();
        sorted.add("DEF/5");
        sorted.add("ABC/10");
        sorted.add("GHI/15");

        List<BarcodesWrapper> barcodesWrapper = bookService.extractBarcodesToBarcodesWrapper(sorted);

        assertEquals(barcodesWrapper.get(0).getBarcode(), "DEF");
        assertEquals(barcodesWrapper.get(1).getBarcode(), "ABC");
        assertEquals(barcodesWrapper.get(2).getBarcode(), "GHI");
    }
}
