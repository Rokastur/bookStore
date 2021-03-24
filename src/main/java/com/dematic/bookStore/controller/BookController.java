package com.dematic.bookStore.controller;

import com.dematic.bookStore.entities.Book;
import com.dematic.bookStore.services.BookService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/v1")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    //A client can use a REST call to put a book into the system
    // providing its name, author, barcode, quantity, price per unit.
    @PostMapping("/books")
    public Book addBook(@RequestBody @Valid BookAuthorDTO dto) {
        return bookService.addNewBook(dto);
    }

    //A client can use a REST call to retrieve book’s information from a system by providing its barcode.
    @GetMapping("/books/{barcode}")
    public EntityModel<Book> getBook(@PathVariable String barcode) {
        Book book = bookService.retrieveBookByBarcode(barcode);
        return EntityModel.of(book,
                linkTo(methodOn(BookController.class).getBook(barcode)).withSelfRel(),
                linkTo(methodOn(BookController.class).getBarcodesForInStockBooks()).withRel("in-stock"));
    }


    //A client can use a REST call to update any of its detail providing the barcode and updated field information.
    @PutMapping("/books/{barcode}")
    public Book updateBook(@RequestBody @Valid BookAuthorDTO dto, @PathVariable String barcode) {
        return bookService.updateBook(barcode, dto);
    }

    //A client can use a REST call to calculate the total price of specific books in the system
    // given the barcode (including antique books and science journals).
    @GetMapping("/books/total-price/{barcode}")
    public BigDecimal getTotalPrice(@PathVariable String barcode) throws Exception {
        return bookService.calculatePriceByBarcode(barcode);
    }

    //A client can use a REST call to request a list of all barcodes for the books in stock grouped by quantity
    @GetMapping("/books/in-stock")
    public CollectionModel<EntityModel<BarcodesWrapper>> getBarcodesForInStockBooks() {
        List<EntityModel<BarcodesWrapper>> barcodes = bookService.barcodesDTOS().stream()
                .map(barcode -> EntityModel.of(barcode,
                        linkTo(methodOn(BookController.class).getBook(barcode.getValue())).withSelfRel(),
                        linkTo(methodOn(BookController.class).getBarcodesForInStockBooks()).withRel("in-stock")))
                .collect(Collectors.toList());
        return CollectionModel.of(barcodes, linkTo(methodOn(BookController.class).getBarcodesForInStockBooks()).withSelfRel());
    }

    //Optional – barcodes for each group sorted by total price
    @GetMapping("/books/barcodes-sorted-by-total-price/{bookType}")
    public ArrayList<String> getBarcodesSortedByTotalPriceAscByBookType(@PathVariable String bookType) {
        return bookService.getBarcodesSortedByTotalPriceByBookType(bookType);
    }
}

