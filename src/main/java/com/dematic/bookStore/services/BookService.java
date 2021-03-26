package com.dematic.bookStore.services;

import com.dematic.bookStore.controller.utility.BarcodesWrapper;
import com.dematic.bookStore.controller.utility.BookAuthorDTO;
import com.dematic.bookStore.entities.AntiqueBook;
import com.dematic.bookStore.entities.Author;
import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.entities.ScienceJournal;
import com.dematic.bookStore.repositories.BookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final PriceOperations priceOperations;

    public BookService(BookRepository bookRepository, AuthorService authorService, PriceOperations priceOperations) {
        this.bookRepository = bookRepository;
        this.authorService = authorService;
        this.priceOperations = priceOperations;
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

    public boolean bookIsScienceJournal(BookAuthorDTO dto) {
        return dto.getScienceIndex() != null;
    }

    public boolean bookIsAntique(BookAuthorDTO dto) {
        return dto.getReleaseYear() != null;
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
        if (bookRepository.existsById(barcode)) {
            return bookRepository.getOneByBarcode(barcode);
        }
        return null;
    }

    public List<BarcodesWrapper> barcodesDTOS() {
        List<String> barcodes = bookRepository.findAllNonNullBarcodesOrderByQuantityDesc();
        List<BarcodesWrapper> barcodesDTOS = new ArrayList<>();
        for (String bar : barcodes) {
            var dto = new BarcodesWrapper();
            dto.setBarcode(bar);
            barcodesDTOS.add(dto);
        }
        return barcodesDTOS;
    }

    public List<BarcodesWrapper> sortAndRetrieveBarcodesByBookType(String bookType) {
        List<String> sortedBarcodes = calculateAndSortPriceForBarcodesOfBookType(bookType);
        return getBarcodesByTotalPriceDesc(sortedBarcodes);
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


    public List<BarcodesWrapper> getBarcodesByTotalPriceDesc(List<String> sorted) {
        List<BarcodesWrapper> barcodesSortedByTotalPriceDesc = new ArrayList<>();
        for (String str : sorted) {
            var barcode = str.substring(0, str.lastIndexOf('/'));
            var wrapper = new BarcodesWrapper();
            wrapper.setBarcode(barcode);
            barcodesSortedByTotalPriceDesc.add(wrapper);
        }
        return barcodesSortedByTotalPriceDesc;
    }


    public BigDecimal calculatePriceByBarcode(String barcode) {
        return priceOperations.calculatePriceByBarcode(barcode);
    }
}
