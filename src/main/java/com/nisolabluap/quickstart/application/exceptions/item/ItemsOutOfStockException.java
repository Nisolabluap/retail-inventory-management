package com.nisolabluap.quickstart.application.exceptions.item;

public class ItemsOutOfStockException extends RuntimeException {
    public ItemsOutOfStockException(String message) {
        super(message);
    }
}
