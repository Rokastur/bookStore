package com.dematic.bookStore.entities;

import com.dematic.bookStore.controller.utility.BookAuthorDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Book_Type")
@DiscriminatorValue("Book")
public class Book {

    public Book() {
    }

    public Book(String ISBN, String title, Integer quantity, BigDecimal unitPrice, Set<Author> authors) {
        this.barcode = convertToBarcode(ISBN);
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        addAuthors(authors);
    }

    public String convertToBarcode(String ISBN) {
        return ISBN.replaceAll("[- ]|^ISBN(?:-1[03])?:?", "");
    }

    @Id
    @Pattern(regexp = "^[0-9]{10}$|^[0-9]{13}$", message = "Invalid barcode, must be 10 or 13 digits")
    protected String barcode;

    @NotEmpty(message = "Title must must not be null nor empty.")
    protected String title;

    @NotNull(message = "Quantity must not be null")
    protected Integer quantity;

    @NotNull(message = "Unit price must not be null")
    @Column(name = "unit_price")
    protected BigDecimal unitPrice;

    @ManyToMany
    @JoinTable(
            name = "Book_Authors",
            joinColumns = @JoinColumn(name = "barcode"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    protected Set<Author> authors = new HashSet<>();

    public void addAuthors(Set<Author> newAuthors) {
        for (Author a : newAuthors) {
            this.authors.add(a);
            a.getBooks().add(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return barcode.equals(book.barcode) && title.equals(book.title) && quantity.equals(book.quantity) && unitPrice.equals(book.unitPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode, title, quantity, unitPrice);
    }
}
