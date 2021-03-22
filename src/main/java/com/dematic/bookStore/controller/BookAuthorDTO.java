package com.dematic.bookStore.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class BookAuthorDTO {

    public static final String ISBNRegex = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";

    @Pattern(regexp = ISBNRegex, message = "Invalid ISBN [DTO]")
    private String barcode;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private String[] authors;
    private Integer scienceIndex;
    private LocalDate releaseYear;

}
