package com.tarasov.market.controller;

import com.tarasov.market.configuration.ResetDB;
import com.tarasov.market.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class OrderControllerTest extends BaseControllerTest {

    @Autowired
    CartRepository cartRepository;

    @Test
    @ResetDB
    public void createNewOrderTest() throws Exception {
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @ResetDB
    public void createNewOrderTest_emptyCart() throws Exception {
        cartRepository.deleteAll().block();
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getOrdersTest() throws Exception {
        webTestClient.get()
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
        webTestClient.get()
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
        webTestClient.get()
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
        webTestClient.get()
                .uri("/orders/0")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void getOrderByIdTest_orderNotExist() throws Exception {
        webTestClient.get()
                .uri("/orders/5")
                .exchange()
                .expectStatus().isNotFound();
    }
}
