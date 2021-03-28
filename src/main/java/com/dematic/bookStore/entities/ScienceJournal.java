package com.dematic.bookStore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@DiscriminatorValue("ScienceJournal")
public class ScienceJournal extends Book {

    public ScienceJournal() {
        super();
    }

    public ScienceJournal(String barcode, String title, Integer quantity, BigDecimal unitPrice, Set<Author> authors, Integer scienceIndex) {
        super(barcode, title, quantity, unitPrice, authors);
        this.scienceIndex = scienceIndex;
    }

    public ScienceJournal(Book book, Integer scienceIndex) {
        this.barcode = book.getBarcode();
        this.title = book.getTitle();
        this.quantity = book.getQuantity();
        this.unitPrice = book.getUnitPrice();
        addAuthors(book.getAuthors());
        this.scienceIndex = scienceIndex;
    }

    @NotNull(message = "Science index must not be null")
    @Column(name = "science_index")
    private Integer scienceIndex;
}
