package com.tarasov.payment.service;


import com.tarasov.payment.model.Account;
import com.tarasov.payment.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Mono<BigDecimal> getAccountBalance(Long id) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Account not exist")))
                .map(Account::getAmount);
    }

    @Override
    public Mono<BigDecimal> updateAccountBalance(Long id, BigDecimal amount) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Account not exist")))
                .flatMap(account -> {
                    account.setAmount(amount);
                    return accountRepository.save(account);
                })
                .map(Account::getAmount);
    }
}
