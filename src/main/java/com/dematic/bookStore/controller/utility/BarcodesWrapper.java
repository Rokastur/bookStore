package com.dematic.bookStore.controller.utility;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BarcodesWrapper {

    public BarcodesWrapper(String barcode) {
        this.barcode = barcode;
    }

    private String barcode;
}
