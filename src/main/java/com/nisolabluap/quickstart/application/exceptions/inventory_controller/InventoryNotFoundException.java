package com.nisolabluap.quickstart.application.exceptions.inventory_controller;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }
}
