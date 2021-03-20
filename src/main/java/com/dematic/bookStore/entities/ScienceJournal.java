package com.dematic.bookStore.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Getter
@Setter
@Entity
@DiscriminatorValue("ScienceJournal")
public class ScienceJournal extends Book {

    @Column(name = "science_index")
    private Integer scienceIndex;
}
