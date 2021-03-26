package com.dematic.bookStore.controller.utility;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class BookAuthorDTO {

    /**
     * valid formats:
     * ISBN 978-0-596-52068-7
     * ISBN-13: 978-0-596-52068-7
     * 978 0 596 52068 7
     * 9780596520687
     * ISBN-10 0-596-52068-9
     * 0-596-52068-9
     */
    public static final String ISBNRegex = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$";

    @Pattern(regexp = ISBNRegex, message = "Invalid ISBN [DTO]")
    private String barcode;
    private String title;
    private Integer quantity;
    private BigDecimal unitPrice;
    private Integer scienceIndex;
    private LocalDate releaseYear;

    private Set<AuthorDTO> authorsDTO = new HashSet<>();

    @Getter
    @Setter
    public static class AuthorDTO {

        private String name;
        private String lastName;
    }

}
