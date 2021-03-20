package com.dematic.bookStore.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Book_Type")
public abstract class Book {

    @Id
    @Column(nullable = false)
    protected String barcode;

    protected String title;

    protected Integer quantity;

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
