package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.utility.BarcodesWrapper;
import com.dematic.bookStore.controller.utility.BookAuthorDTO;
import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Author;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import com.dematic.bookStore.services.exceptions.BookNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final PriceOperations priceOperations;
    private final BookTypeConversionOperations conversionOperations;

    public BookService(BookRepository bookRepository, AuthorService authorService, PriceOperations priceOperations, BookTypeConversionOperations conversionOperations) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.priceOperations = priceOperations;
        this.conversionOperations = conversionOperations;
    }

    public Book updateBook(String barcode, BookAuthorDTO dto) {
        Set<Author> authors = authorService.retrieveFromDbOrCreateNew(dto.getAuthorsDTO());
        Book book = retrieveBookByBarcode(barcode);

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

        //if dto contains scienceIndex, release year will be ignored
        if (book instanceof ScienceJournal || dto.getScienceIndex() != null) {
            book = conversionOperations.updateScienceIndexOrConvertBookType(book, dto);
        } else if (book instanceof AntiqueBook || dto.getReleaseYear() != null) {
            book = conversionOperations.updateReleaseYearOrConvertBookType(book, dto);
        }
        return bookRepository.save(book);
    }

    public Book addNewBook(BookAuthorDTO dto) {
        Set<Author> authors = authorService.retrieveFromDbOrCreateNew(dto.getAuthorsDTO());
        Book book;
        //dto may have both science index and a release date. Science index takes precedence over the release date,
        //therefore if dto contains science index, ScienceJournal will be created instead of AntiqueBook or Book.
        if (bookIsScienceJournal(dto)) {
            book = createNewScienceJournal(dto, authors);
        } else if (bookIsAntique(dto)) {
            book = createNewAntiqueBook(dto, authors);
        } else {
            book = createNewRegularBook(dto, authors);
        }

        String barcode = book.getBarcode();
        if (barcode != null) {
            String b = barcode.replaceAll("[- ]|^ISBN(?:-1[03])?:?", "");
            book.setBarcode(b);
        }
        return bookRepository.save(book);
    }

    public boolean bookIsScienceJournal(BookAuthorDTO dto) {
        return dto.getScienceIndex() != null;
    }

    public boolean bookIsAntique(BookAuthorDTO dto) {
        LocalDate threshold = LocalDate.parse("1900-01-01");
        return dto.getReleaseYear() != null && dto.getReleaseYear().isBefore(threshold);
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

    public Book retrieveBookByBarcode(String barcode) {
        return bookRepository.findById(barcode)
                .orElseThrow(() -> new BookNotFoundException("book with barcode " + barcode + " was not found"));
    }

    public List<BarcodesWrapper> getBarcodesWrapperForTheBooksInStock() {
        List<String> barcodes = bookRepository.findAllNonNullBarcodesOrderByQuantityDesc();
        List<BarcodesWrapper> barcodesDTOS = new ArrayList<>();
        for (String bar : barcodes) {
            var dto = new BarcodesWrapper(bar);
            barcodesDTOS.add(dto);
        }
        return barcodesDTOS;
    }

    public List<BarcodesWrapper> sortAndRetrieveBarcodesByBookType(String bookType) {
        List<String> sortedBarcodes = calculateAndSortPriceForBarcodesOfBookType(bookType);
        return extractBarcodesToBarcodesWrapper(sortedBarcodes);
    }

    public List<String> calculateAndSortPriceForBarcodesOfBookType(String bookType) {
        Set<String> barcodesByBookType = bookRepository.findAllBarcodesByBookType(bookType);
        List<String> toSort = new ArrayList<>();
        for (String barcode : barcodesByBookType) {
            String combined = barcode + "/" + calculatePriceByBarcode(barcode);
            toSort.add(combined);
        }
        toSort.sort(new TotalPriceComparator());
        return toSort;
    }


    public List<BarcodesWrapper> extractBarcodesToBarcodesWrapper(List<String> sorted) {
        List<BarcodesWrapper> barcodesSortedByTotalPriceDesc = new ArrayList<>();
        for (String str : sorted) {
            var barcode = str.substring(0, str.lastIndexOf('/'));
            var wrapper = new BarcodesWrapper(barcode);
            barcodesSortedByTotalPriceDesc.add(wrapper);
        }
        return barcodesSortedByTotalPriceDesc;
    }


    public BigDecimal calculatePriceByBarcode(String barcode) {
        return priceOperations.calculatePriceByBarcode(barcode);
    }
}
