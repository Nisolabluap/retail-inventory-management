package com.nisolabluap.quickstart.application.exceptions.order;

public class InvalidQuantitiesException extends RuntimeException {
    public InvalidQuantitiesException(String message) {
        super(message);
    }
}
