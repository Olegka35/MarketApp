package com.tarasov.market.service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PaymentService {
    Mono<BigDecimal> getAccountBalance();
    Mono<BigDecimal> makePayment(BigDecimal amount);
}
