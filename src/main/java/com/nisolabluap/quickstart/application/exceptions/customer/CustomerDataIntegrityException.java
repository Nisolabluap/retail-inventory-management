package com.nisolabluap.quickstart.application.exceptions.customer;

public class CustomerDataIntegrityException extends RuntimeException {
    public CustomerDataIntegrityException(String message) {
        super(message);
    }
}
