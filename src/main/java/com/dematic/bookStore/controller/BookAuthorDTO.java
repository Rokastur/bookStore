package com.dematic.bookStore.controller;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookAuthorDTO {

    private String barcode;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String[] authors;
    private Integer scienceIndex;
    private LocalDate releaseYear;

}
