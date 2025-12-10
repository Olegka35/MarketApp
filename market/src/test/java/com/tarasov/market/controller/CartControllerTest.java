package com.tarasov.market.controller;

import com.tarasov.market.configuration.ResetDB;
import com.tarasov.market.repository.CartRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


class CartControllerTest extends BaseControllerTest {

    @Autowired
    CartRepository cartRepository;

    @BeforeEach
    void setUp() {
        cartRepository.findAllWithOffering().collectList().subscribe(System.out::println);
    }

    @Test
    public void getCartItemsTest() {
        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("/wireless_mouse.jpg"));
                    assertTrue(html.contains("3170 руб."));
                    assertTrue(html.contains("Зонт складной"));
                    assertTrue(html.contains("/umbrella.jpg"));
                    assertTrue(html.contains("990 руб."));
                });
    }

    @Test
    @ResetDB
    public void addCartItemFromCartPageTest() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "1")
                        .queryParam("action", "PLUS")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Термокружка 500 мл"));
                    assertTrue(html.contains("/thermocup.jpg"));
                    assertTrue(html.contains("1290 руб."));
                    assertTrue(html.contains("<span>1</span>"));
                    assertTrue(html.contains("Итого: 4460 руб."));
                });
    }

    @Test
    @ResetDB
    public void removeCartItemFromCartPageTest() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "2")
                        .queryParam("action", "MINUS")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("/wireless_mouse.jpg"));
                    assertTrue(html.contains("990 руб."));
                    assertTrue(html.contains("<span>1</span>"));
                    assertTrue(html.contains("Итого: 2180 руб."));
                });
    }

    @Test
    @ResetDB
    public void removeCartItemToZeroFromCartPageTest() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "5")
                        .queryParam("action", "MINUS")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertFalse(html.contains("Зонт складной"));
                    assertTrue(html.contains("Итого: 1980 руб."));
                    assertTrue(html.contains("Беспроводная мышь"));
                });
    }

    @Test
    @ResetDB
    public void deleteCartItemFromCartPageTest() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "2")
                        .queryParam("action", "DELETE")
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/cart/items");

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertFalse(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Итого: 1190 руб."));
                    assertTrue(html.contains("Зонт складной"));
                });
    }

    @Test
    @ResetDB
    public void deleteCartItemFromCartPageTest_notExistInCart() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "1")
                        .queryParam("action", "DELETE")
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @ResetDB
    public void addCartItemFromCartPageTest_incorrectOfferingId() {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", "200")
                        .queryParam("action", "PLUS")
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @CsvSource({
            "0, PLUS",
            "-1, MINUS",
            "2, ",
            "5, ADD",
            "5, REMOVE"
    })
    @ResetDB
    public void updateCartItemFromCartPageTest_badRequest(String id,
                                                          String action) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/cart/items")
                        .queryParam("id", id)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @CsvSource({
            "1, , , , 3, PLUS",
            "2, , , 2, 3, MINUS",
            "5, , , , , MINUS",
            "5, Зонт, PRICE, 1, 5, PLUS",
            "5, , , , , DELETE"
    })
    @ResetDB
    public void updateCartItemFromMainPageTest(String id,
                                               String search,
                                               String sort,
                                               String pageNumber,
                                               String pageSize,
                                               String action) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", id)
                        .queryParam("search", search)
                        .queryParam("sort", sort)
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location",
                        String.format("/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                                URLEncoder.encode(Optional.ofNullable(search).orElse(""), StandardCharsets.UTF_8),
                                Optional.ofNullable(sort).orElse("NO"),
                                Integer.valueOf(Optional.ofNullable(pageNumber).orElse("1")),
                                Integer.valueOf(Optional.ofNullable(pageSize).orElse("5")))
                );
    }

    @ParameterizedTest
    @CsvSource({
            "0, , , , 3, PLUS",
            "1, , , 2, -1, MINUS",
            "5, , TITLE, , , MINUS",
            "5, Зонт, PRICE, 1, 5, ADD",
            "5, , , 0, , DELETE"
    })
    @ResetDB
    public void updateCartItemFromMainPageTest_badRequest(String id,
                                                          String search,
                                                          String sort,
                                                          String pageNumber,
                                                          String pageSize,
                                                          String action) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", id)
                        .queryParam("search", search)
                        .queryParam("sort", sort)
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @CsvSource({
            "100, , , , 3, PLUS",
            "4, , , 2, 3, MINUS",
            "1, , , , , DELETE"
    })
    @ResetDB
    public void updateCartItemFromMainPageTest_notFound(String id,
                                                          String search,
                                                          String sort,
                                                          String pageNumber,
                                                          String pageSize,
                                                          String action) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items")
                        .queryParam("id", id)
                        .queryParam("search", search)
                        .queryParam("sort", sort)
                        .queryParam("pageNumber", pageNumber)
                        .queryParam("pageSize", pageSize)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound();
    }

    @ParameterizedTest
    @CsvSource({
            "1, PLUS, 1, 1290, Термокружка 500 мл, /thermocup.jpg",
            "5, PLUS, 2, 1190, Зонт складной, /umbrella.jpg",
            "2, MINUS, 1, 990, Беспроводная мышь, /wireless_mouse.jpg",
            "2, DELETE, 0, 990, Беспроводная мышь, /wireless_mouse.jpg",
            "5, DELETE, 0, 1190, Зонт складной, /umbrella.jpg",
    })
    @ResetDB
    public void updateCartItemFromOfferingPageTest(String id,
                                                   String action,
                                                   String newCount,
                                                   String price,
                                                   String title,
                                                   String imgPath) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/" + id)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().is3xxRedirection();

        webTestClient.get()
                .uri("/items/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains(title));
                    assertTrue(html.contains(imgPath));
                    assertTrue(html.contains(price));
                    assertTrue(html.contains("<span>" + newCount + "</span>"));
                });
    }

    @ParameterizedTest
    @CsvSource({
            "0, PLUS",
            "5, ADD",
            "-1, MINUS",
            "2, REMOVE"
    })
    @ResetDB
    public void updateCartItemFromOfferingPageTest_badRequest(String id,
                                                              String action) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/" + id)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().isBadRequest();
    }

    @ParameterizedTest
    @CsvSource({
            "100, PLUS",
            "1, DELETE",
            "1, MINUS"
    })
    @ResetDB
    public void updateCartItemFromOfferingPageTest_notFound(String id,
                                                              String action) {
        webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("/items/" + id)
                        .queryParam("action", action)
                        .build()
                )
                .exchange()
                .expectStatus().isNotFound();
    }
}
