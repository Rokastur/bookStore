package com.dematic.bookStore.entities;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Book_Type")
@DiscriminatorValue("Book")
public abstract class Book {

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

    public void addAuthors(Set<Author> authors) {
        for (Author a : authors) {
            authors.add(a);
            a.getBooks().add(this);
        }
    }


}
