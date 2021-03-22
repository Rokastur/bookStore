package com.dematic.bookStore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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

    public Book(String barcode, String title, Integer quantity, BigDecimal unitPrice, Set<Author> authors) {
        this.barcode = barcode;
        this.title = title;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.authors = authors;
    }

    @Id
    @Column(nullable = false)
    protected String barcode;

    protected String title;

    protected Integer quantity;

    @Column(name = "unit_price")
    protected BigDecimal unitPrice;


    @ManyToMany
    @JoinTable(
            name = "Book_Authors",
            joinColumns = @JoinColumn(name = "barcode"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    protected Set<Author> authors = new HashSet<>();

    public void addAuthor(Author author) {
        this.authors.add(author);
        author.getBooks().add(this);
    }

    public void removeAuthor(Author author) {
        this.authors.remove(author);
        author.getBooks().remove(this);
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
