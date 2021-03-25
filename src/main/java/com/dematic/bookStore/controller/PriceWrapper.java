package com.dematic.bookStore.controller;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceWrapper {

    public PriceWrapper(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    private BigDecimal totalPrice;
}
