package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.BookAuthorDTO;
import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Author;
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
    private final AuthorService authorService;

    public BookService(BookRepository bookRepository, AuthorService authorService) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
    }

    public boolean bookIsScienceJournal(BookAuthorDTO dto) {
        return dto.getScienceIndex() != null;
    }

    public boolean bookIsAntique(BookAuthorDTO dto) {
        return dto.getReleaseYear() != null;
    }


    public Book updateBook(String barcode, BookAuthorDTO dto) {
        Set<Author> authors = authorService.retrieveOrCreateAuthorsFromDB(dto.getAuthorsDTO());
        Book book = bookRepository.getOneByBarcode(barcode);

        if (dto.getBarcode() != null) {
            book.setBarcode(dto.getBarcode());
        }
        if (dto.getQuantity() != null) {
            book.setQuantity(dto.getQuantity());
        }
        if (dto.getTitle() != null) {
            book.setTitle(dto.getTitle());
        }
        if (dto.getUnitPrice() != null) {
            book.setUnitPrice(dto.getUnitPrice());
        }
        if (!authors.isEmpty()) {
            book.addAuthors(authors);
        }
        if (book instanceof ScienceJournal && dto.getScienceIndex() != null) {
            ((ScienceJournal) book).setScienceIndex(dto.getScienceIndex());
        }
        if (book instanceof AntiqueBook && dto.getReleaseYear() != null) {
            ((AntiqueBook) book).setReleaseYear(dto.getReleaseYear());
        }
        return bookRepository.save(book);
    }

    public Book addNewBook(BookAuthorDTO dto) {
        Set<Author> authors = authorService.retrieveOrCreateAuthorsFromDB(dto.getAuthorsDTO());
        Book book;
        if (bookIsScienceJournal(dto)) {
            book = createNewScienceJournal(dto, authors);
        } else if (bookIsAntique(dto)) {
            book = createNewAntiqueBook(dto, authors);
        } else {
            book = createNewRegularBook(dto, authors);
        }
        return bookRepository.save(book);
    }

    public Book createNewScienceJournal(BookAuthorDTO dto, Set<Author> authors) {
        return new ScienceJournal(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors, dto.getScienceIndex());
    }

    public Book createNewAntiqueBook(BookAuthorDTO dto, Set<Author> authors) {
        return new AntiqueBook(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors, dto.getReleaseYear());
    }

    public Book createNewRegularBook(BookAuthorDTO dto, Set<Author> authors) {
        return new Book(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors);
    }

    public Book retrieveBookByBarcode(String barcode) throws Exception {
        if (bookRepository.existsById(barcode)) {
            return bookRepository.getOneByBarcode(barcode);
        } else throw new Exception("book with the barcode: " + barcode + " not found");
    }

    public Set<String> listBarcodesForTheInStockBooksGroupedByQuantity() {
        return bookRepository.findAllBarcodesOrderByNonNullQuantityDesc();
    }

    public ArrayList<String> getBarcodesSortedByTotalPriceByBookType(String bookType) throws Exception {
        Set<String> barcodesByBookType = bookRepository.findAllBarcodesByBookType(bookType);
        ArrayList<String> toSort = new ArrayList<>();
        for (String barcode : barcodesByBookType) {
            String combined = barcode + "/" + calculatePriceByBarcode(barcode);
            toSort.add(combined);
        }
        toSort.sort(new TotalPriceComparator());

        ArrayList<String> barcodesByTotalPriceDesc = new ArrayList<>();
        for (String str : toSort) {
            var barcode = str.substring(0, str.lastIndexOf('/'));
            barcodesByTotalPriceDesc.add(barcode);
        }

        return barcodesByTotalPriceDesc;
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

    public BigDecimal calculateNonIndexedPrice(Book book) {
        var quantity = new BigDecimal(book.getQuantity());
        var nonRoundedTotalPrice = book.getUnitPrice().multiply(quantity);
        var roundedTotalPrice = nonRoundedTotalPrice.setScale(2, RoundingMode.HALF_UP);
        return roundedTotalPrice;

    }
}
