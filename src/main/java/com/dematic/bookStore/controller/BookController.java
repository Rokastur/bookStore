package com.dematic.bookStore.controller;

import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.services.BookService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/v1")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping("/books")
    public Book addBook(@RequestBody Book newBook) {
        return bookService.addNewBook(newBook);
    }

    @GetMapping("/books/{barcode}")
    public Book getBook(@PathVariable String barcode) throws Exception {
        return bookService.retrieveBookByBarcode(barcode);
    }

    @PutMapping("/books/{barcode}")
    public Book updateBook(@RequestBody Book newBook, @PathVariable String barcode) throws Exception {
        return bookService.updateBook(barcode, newBook);
    }

    @GetMapping("/books/total-price/{barcode}")
    public BigDecimal getTotalPrice(@PathVariable String barcode) throws Exception {
        return bookService.calculatePriceByBarcode(barcode);
    }

    @GetMapping("/books/in-stock")
    public Set<String> getBarcodesForInStockBooks() {
        return bookService.listBarcodesForTheInStockBooksGroupedByQuantity();
    }

    @GetMapping("/books/barcodes-sorted-by-total-price/{bookType}")
    public ArrayList<String> getBarcodesSortedByTotalPriceByBookType(@PathVariable String bookType) throws Exception {
        return bookService.getBarcodesSortedByTotalPriceByBookType(bookType);
    }
}

