package com.nisolabluap.quickstart.application.exceptions.customer;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
