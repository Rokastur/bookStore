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

    public Book addNewBook(BookAuthorDTO dto) throws Exception {

        String[] dtoAuthors = dto.getAuthors();
        Set<Author> authors = new HashSet<>();

        for (String author : dtoAuthors) {
            String firstName = extractFirstName(author);
            String lastName = extractLastName(author);
            if (!authorService.exists(firstName, lastName)) {
                Author a = new Author(firstName, lastName);
                authorService.saveAuthor(a);
            }
            authors.add(authorService.findByFullName(firstName, lastName));
        }

        Book book;
        if (bookIsScienceJournal(dto) && bookIsAntique(dto)) {
            throw new Exception("book can not be both an antique and a science journal");
        }
        if (bookIsScienceJournal(dto)) {
            book = new ScienceJournal(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors, dto.getScienceIndex());
        } else if (bookIsAntique(dto)) {
            book = new AntiqueBook(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors, dto.getReleaseYear());
        } else {
            book = new Book(dto.getBarcode(), dto.getTitle(), dto.getQuantity(), dto.getUnitPrice(), authors);
        }

        return bookRepository.save(book);
    }

    public String extractFirstName(String author) {
        int spaceLocation = author.indexOf(' ');
        return author.substring(0, spaceLocation).strip();
    }

    public String extractLastName(String author) {
        int spaceLocation = author.indexOf(' ');
        int length = author.length();
        return author.substring(spaceLocation, length).strip();
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
        var nonRoundedTotalPrice = book.getUnitPrice().multiply(quantity);
        var roundedTotalPrice = nonRoundedTotalPrice.setScale(2, RoundingMode.HALF_UP);
        return roundedTotalPrice;

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
