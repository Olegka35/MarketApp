package com.tarasov.payment.repository;


import com.tarasov.payment.configuration.BalanceResetExtension;
import com.tarasov.payment.configuration.ResetBalance;
import com.tarasov.payment.model.Account;
import com.tarasov.payment.configuration.PostgresTestcontainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
@ExtendWith(BalanceResetExtension.class)
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findAccountByIdTest() {
        Account account = accountRepository.findById(1L).block();
        assertNotNull(account);
        assertEquals(1L, account.getId());
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000L));
    }

    @Test
    void findNonExistingAccountByIdTest() {
        Account account = accountRepository.findById(5L).block();
        assertNull(account);
    }

    @Test
    @ResetBalance
    void updateAccountBalanceTest() {
        Account account = accountRepository.findById(1L).block();
        assertNotNull(account);
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000L));

        account.setAmount(BigDecimal.valueOf(2000L));

        Account updatedAccount = accountRepository.save(account).block();
        assertNotNull(updatedAccount);
        assertThat(updatedAccount.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(2000L));
    }

    @Test
    @ResetBalance
    void updateAccountToZeroBalanceTest() {
        Account account = accountRepository.findById(1L).block();
        assertNotNull(account);
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000L));

        account.setAmount(BigDecimal.ZERO);

        Account updatedAccount = accountRepository.save(account).block();
        assertNotNull(updatedAccount);
        assertThat(updatedAccount.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @ResetBalance
    void updateAccountToNegativeBalanceTest() {
        Account account = accountRepository.findById(1L).block();
        assertNotNull(account);
        assertThat(account.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(5000L));

        account.setAmount(BigDecimal.valueOf(-2000L));

        assertThrows(Throwable.class,() -> accountRepository.save(account).block());
    }
}
