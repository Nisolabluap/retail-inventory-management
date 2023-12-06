package com.nisolabluap.quickstart.application.exceptions.item;

public class DuplicateItemException extends RuntimeException {
    public DuplicateItemException(String message) {
        super(message);
    }
}
