package com.nisolabluap.quickstart.application.exceptions.inventory_controller;

public class DuplicateInventoryItemException extends RuntimeException {
    public DuplicateInventoryItemException(String message) {
        super(message);
    }
}
