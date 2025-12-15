package com.tarasov.market.model.exception;

public class PaymentException extends RuntimeException {
    public PaymentException() {
        super("Error during payment for order");
    }
}
