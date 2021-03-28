package com.dematic.bookStore.controller.utility;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.*;
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

    private final String genericMessage = "cannot create new publication without providing ";

    @Pattern(groups = {NewEntity.class}, regexp = ISBNRegex, message = "Invalid ISBN")
    @NotNull(groups = {NewEntity.class}, message = genericMessage + "barcode")
    private String barcode;

    @NotEmpty(groups = {NewEntity.class}, message = genericMessage + "title")
    private String title;

    @PositiveOrZero(message = "book must have a quantity of 0 or more")
    @NotNull(groups = {NewEntity.class}, message = genericMessage + "quantity")
    private Integer quantity;

    @PositiveOrZero(message = "book must have a unit price value of 0 or more")
    @NotNull(groups = {NewEntity.class}, message = genericMessage + "unitPrice")
    private BigDecimal unitPrice;

    @Range(min = 1, max = 10, message = "science index must be between 1 and 10, inclusive")
    private Integer scienceIndex;

    @PastOrPresent(message = "only released books may be added")
    private LocalDate releaseYear;

    @NotEmpty(groups = {NewEntity.class}, message = genericMessage + "at least one author")
    private Set<AuthorDTO> authorsDTO = new HashSet<>();

    @Getter
    @Setter
    public static class AuthorDTO {

        private String name;
        private String lastName;
    }

}
