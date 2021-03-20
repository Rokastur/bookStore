package com.dematic.bookStore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import java.time.LocalDate;

@Getter
@Setter
@DiscriminatorValue("AntiqueBook")
public class AntiqueBook extends Book {

    @Column(name = "release_year")
    private LocalDate releaseYear;

}
