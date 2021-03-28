package com.dematic.bookStore.services.exceptions;

public class InvalidBookType extends RuntimeException {
    public InvalidBookType(String message) {
        super(message);
    }
}
