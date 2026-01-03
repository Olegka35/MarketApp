package com.tarasov.payment.controller;

import com.tarasov.payment.configuration.BalanceResetExtension;
import com.tarasov.payment.configuration.PostgresTestcontainer;
import com.tarasov.payment.configuration.ResetBalance;
import com.tarasov.payment.model.dto.BalanceDto;
import com.tarasov.payment.model.dto.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureWebTestClient
@Testcontainers
@ImportTestcontainers(PostgresTestcontainer.class)
@ExtendWith(BalanceResetExtension.class)
public class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @WithMockUser(authorities = "ACCOUNT")
    public void getAccountBalanceTest() {
        webTestClient.get()
                .uri("/account/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceDto.class)
                .value(dto -> {
                    assertNotNull(dto);
                    assertThat(dto.balance()).isEqualByComparingTo(BigDecimal.valueOf(5000));
                });
    }

    @Test
    @WithMockUser(authorities = "ACCOUNT")
    public void getNonExistingAccountBalanceTest() {
        webTestClient.get()
                .uri("/account/2")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(authorities = "ACCOUNT")
    public void getIncorrectAccountBalanceTest() {
        webTestClient.get()
                .uri("/account/-2")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @ResetBalance
    @WithMockUser(authorities = "ACCOUNT")
    public void makePaymentTest() {
        webTestClient.post()
                .uri("/account/1/payment")
                .bodyValue(new PaymentRequest(BigDecimal.valueOf(1)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceDto.class)
                .value(dto -> {
                    assertNotNull(dto);
                    assertThat(dto.balance()).isEqualByComparingTo(BigDecimal.valueOf(4999));
                });
    }

    @Test
    @ResetBalance
    @WithMockUser(authorities = "ACCOUNT")
    public void makePaymentToZeroTest() {
        webTestClient.post()
                .uri("/account/1/payment")
                .bodyValue(new PaymentRequest(BigDecimal.valueOf(5000)))
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceDto.class)
                .value(dto -> {
                    assertNotNull(dto);
                    assertThat(dto.balance()).isEqualByComparingTo(BigDecimal.ZERO);
                });
    }

    @Test
    @ResetBalance
    @WithMockUser(authorities = "ACCOUNT")
    public void makeIncorrectPaymentTest() {
        webTestClient.post()
                .uri("/account/1/payment")
                .bodyValue(new PaymentRequest(BigDecimal.valueOf(-10)))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @ResetBalance
    @WithMockUser(authorities = "ACCOUNT")
    public void makePaymentForNonExistingAccountTest() {
        webTestClient.post()
                .uri("/account/100/payment")
                .bodyValue(new PaymentRequest(BigDecimal.valueOf(1000)))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @ResetBalance
    @WithMockUser(authorities = "ACCOUNT")
    public void makePaymentWithInsufficientAmountTest() {
        webTestClient.post()
                .uri("/account/1/payment")
                .bodyValue(new PaymentRequest(BigDecimal.valueOf(5001)))
                .exchange()
                .expectStatus().value(status -> assertEquals(402, status));
    }

    @Test
    public void getAccountBalanceTest_unauthorized() {
        webTestClient.get()
                .uri("/account/1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser(authorities = "USER")
    public void getAccountBalanceTest_noGrants() {
        webTestClient.get()
                .uri("/account/1")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    public void makePaymentTest_unauthorized() {
        webTestClient.post()
                .uri("/account/1/payment")
                .bodyValue(new PaymentRequest(BigDecimal.valueOf(1)))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
