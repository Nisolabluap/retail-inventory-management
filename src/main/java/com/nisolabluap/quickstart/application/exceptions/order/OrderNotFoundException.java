package com.nisolabluap.quickstart.application.exceptions.order;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
