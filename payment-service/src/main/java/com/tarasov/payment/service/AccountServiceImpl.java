package com.tarasov.payment.service;


import com.tarasov.payment.model.Account;
import com.tarasov.payment.model.UnsufficientBalanceException;
import com.tarasov.payment.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public Mono<BigDecimal> updateAccountBalance(Long id, BigDecimal amount) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Account not exist")))
                .flatMap(account -> processBalanceUpdate(account, amount))
                .map(Account::getAmount);
    }

    @Override
    @Transactional
    public Mono<BigDecimal> deductBalance(Long id, BigDecimal value) {
        return accountRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Account not exist")))
                .flatMap(account -> {
                    if (account.getAmount().compareTo(value) < 0) {
                        return Mono.error(new UnsufficientBalanceException("Balance not enough for the operation"));
                    }
                    return processBalanceUpdate(account, account.getAmount().subtract(value));
                })
                .map(Account::getAmount);
    }

    private Mono<Account> processBalanceUpdate(Account account, BigDecimal amount) {
        account.setAmount(amount);
        return accountRepository.save(account);
    }
}
