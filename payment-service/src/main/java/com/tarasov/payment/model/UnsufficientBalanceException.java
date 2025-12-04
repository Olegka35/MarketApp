package com.tarasov.payment.model;

public class UnsufficientBalanceException extends IllegalStateException {
    public UnsufficientBalanceException(String message) {
        super(message);
    }
}
