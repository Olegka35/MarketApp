package com.tarasov.payment.model.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(@Positive BigDecimal amount) {
}
