package com.nisolabluap.quickstart.application.exceptions.item;

public class ItemsNotFoundException extends RuntimeException {
    public ItemsNotFoundException(String message) {
        super(message);
    }
}
