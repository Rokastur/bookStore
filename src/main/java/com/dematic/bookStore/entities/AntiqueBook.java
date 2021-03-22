package com.dematic.bookStore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Entity
@DiscriminatorValue("AntiqueBook")
public class AntiqueBook extends Book {

    public AntiqueBook(String barcode, String title, Integer quantity, BigDecimal unitPrice, Set<Author> authors, LocalDate releaseYear) {
        super(barcode, title, quantity, unitPrice, authors);
        this.releaseYear = releaseYear;
    }

    public AntiqueBook() {
        super();
    }

    @Column(name = "release_year")
    private LocalDate releaseYear;
}
