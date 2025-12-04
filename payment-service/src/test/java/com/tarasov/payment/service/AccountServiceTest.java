package com.tarasov.payment.service;

import com.tarasov.payment.model.Account;
import com.tarasov.payment.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void getAccountBalanceTest() {
        Account mockAccount = new Account(1L, BigDecimal.valueOf(5000));
        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(mockAccount));

        BigDecimal amount = accountService.getAccountBalance(1L).block();

        assertEquals(BigDecimal.valueOf(5000), amount);
    }

    @Test
    public void getNonExistingAccountBalanceTest() {
        when(accountRepository.findById(1L)).thenReturn(Mono.empty());

        assertThrows(NoSuchElementException.class, () -> accountService.getAccountBalance(1L).block());
    }

    @Test
    public void updateAccountBalanceTest() {
        Account mockAccount = new Account(1L, BigDecimal.valueOf(5000));
        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(mockAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        BigDecimal amount = accountService.updateAccountBalance(1L, BigDecimal.ONE).block();

        assertEquals(BigDecimal.ONE, amount);
    }

    @Test
    public void updateNonExistingAccountBalanceTest() {
        when(accountRepository.findById(1L)).thenReturn(Mono.empty());

        assertThrows(NoSuchElementException.class,
                () -> accountService.updateAccountBalance(1L, BigDecimal.TEN).block());
    }

    @Test
    public void deductBalanceTest() {
        Account mockAccount = new Account(1L, BigDecimal.valueOf(5000));
        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(mockAccount));
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        BigDecimal amount = accountService.deductBalance(1L, BigDecimal.valueOf(200)).block();

        assertEquals(BigDecimal.valueOf(4800), amount);
    }

    @Test
    public void deductNotEnoughBalanceTest() {
        Account mockAccount = new Account(1L, BigDecimal.valueOf(5000));
        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(mockAccount));

        assertThrows(IllegalStateException.class,
                () -> accountService.deductBalance(1L, BigDecimal.valueOf(5001)).block());
    }

    @Test
    public void deductNonExistingAccountBalanceTest() {
        when(accountRepository.findById(1L)).thenReturn(Mono.empty());

        assertThrows(NoSuchElementException.class,
                () -> accountService.updateAccountBalance(1L, BigDecimal.TEN).block());
    }
}
