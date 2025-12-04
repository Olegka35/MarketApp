package com.tarasov.payment.service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface AccountService {
    Mono<BigDecimal> getAccountBalance(Long id);
    Mono<BigDecimal> updateAccountBalance(Long id, BigDecimal amount);
    Mono<BigDecimal> deductBalance(Long id, BigDecimal value);
}
