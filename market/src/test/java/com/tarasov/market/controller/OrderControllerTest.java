package com.tarasov.market.controller;

import com.tarasov.market.configuration.ResetDB;
import com.tarasov.market.model.BalanceInfo;
import com.tarasov.market.model.PaymentRequest;
import com.tarasov.market.model.TestUserContext;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class OrderControllerTest extends BaseControllerTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(paymentService, "paymentApi", paymentApi);
    }

    @Test
    @ResetDB
    public void createNewOrderTest() throws Exception {
        when(paymentApi.makePayment(eq(1L), any()))
                .thenReturn(Mono.just(new BalanceInfo().balance(BigDecimal.ONE)));
        webTestClient.mutateWith(TestUserContext.mockUser()).post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
        verify(paymentApi).makePayment(1L, new PaymentRequest().amount(BigDecimal.valueOf(3170)));
    }

    @Test
    @ResetDB
    public void createNewOrderTest_emptyCart() throws Exception {
        cartRepository.deleteAll().block();
        webTestClient.mutateWith(TestUserContext.mockUser()).post()
                .uri("/buy")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @ResetDB
    public void createNewOrderTest_unsufficientBalance() throws Exception {
        when(paymentApi.makePayment(eq(1L), any()))
                .thenReturn(Mono.error(new Exception()));
        webTestClient.mutateWith(TestUserContext.mockUser()).post()
                .uri("/buy")
                .exchange()
                .expectStatus().is4xxClientError();
        verify(paymentApi).makePayment(1L, new PaymentRequest().amount(BigDecimal.valueOf(3170)));
    }

    @Test
    public void getOrdersTest() throws Exception {
        webTestClient.mutateWith(TestUserContext.mockUser()).get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Заказ №1"));
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Рюкзак городской"));
                    assertTrue(html.contains("Сумма: 1000 руб."));
                });
    }

    @Test
    public void getOrderByIdTest() throws Exception {
        webTestClient.mutateWith(TestUserContext.mockUser()).get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Заказ №1"));
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Рюкзак городской"));
                    assertTrue(html.contains("Сумма: 1000 руб."));
                    assertFalse(html.contains("Поздравляем! Успешная покупка!"));
                });
    }

    @Test
    public void getOrderByIdAfterCreationTest() throws Exception {
        webTestClient.mutateWith(TestUserContext.mockUser()).get()
                .uri("/orders/1?newOrder=true")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Заказ №1"));
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Рюкзак городской"));
                    assertTrue(html.contains("Сумма: 1000 руб."));
                    assertTrue(html.contains("Поздравляем! Успешная покупка!"));
                });
    }

    @Test
    public void getOrderByIdTest_incorrectId() throws Exception {
        webTestClient.mutateWith(TestUserContext.mockUser()).get()
                .uri("/orders/0")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void getOrderByIdTest_orderNotExist() throws Exception {
        webTestClient.mutateWith(TestUserContext.mockUser()).get()
                .uri("/orders/5")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @ResetDB
    public void createNewOrderTest_unauthorized() throws Exception {
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/login");
    }

    @Test
    public void getOrdersTest_unauthorized() throws Exception {
        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/login");
    }

    @Test
    public void getOrderByIdTest_unauthorized() throws Exception {
        webTestClient.get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isFound()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/login");
    }

    @Test
    public void getOrderByIdTest_someoneElseOrder() throws Exception {
        webTestClient
                .mutateWith(TestUserContext.configureTestUser(2L, "user", "password", "USER"))
                .get()
                .uri("/orders/1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
