package com.dematic.bookStore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@DiscriminatorValue("AntiqueBook")
public class AntiqueBook extends Book {

    private LocalDate releaseYear;
}