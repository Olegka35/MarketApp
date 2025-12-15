package com.tarasov.market.model.type;

import lombok.Getter;

@Getter
public enum PaymentError {
    INSUFFICIENT_BALANCE ("Not enough balance"),
    PAYMENT_SERVICE_NOT_AVAILABLE ("Payment service not available");

    private final String message;

    PaymentError(String message) {
        this.message = message;
    }
}
