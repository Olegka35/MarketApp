package com.tarasov.market.service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PaymentService {
    Mono<BigDecimal> getAccountBalance(Long accountId);
    Mono<BigDecimal> makePayment(Long accountId, BigDecimal amount);
}
