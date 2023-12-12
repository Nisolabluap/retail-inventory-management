package com.nisolabluap.quickstart.application.exceptions.order;

public class OrderAlreadyRefundedException extends RuntimeException {
    public OrderAlreadyRefundedException(String message) {
        super(message);
    }
}
