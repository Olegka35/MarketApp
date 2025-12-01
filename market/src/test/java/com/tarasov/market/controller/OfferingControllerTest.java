package com.tarasov.market.controller;


import com.tarasov.market.configuration.ResetDB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.reactive.function.BodyInserters;

import static org.junit.jupiter.api.Assertions.*;

public class OfferingControllerTest extends BaseControllerTest {

    @Test
    public void searchOfferingsTest() {
        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Термокружка"));
                    assertTrue(html.contains("Зонт складной"));
                    assertTrue(html.contains("Настольная лампа"));
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Рюкзак городской"));
                    assertTrue(html.contains("Страница: 1"));
                    assertFalse(html.contains("&larr;</button>"));
                    assertFalse(html.contains("&rarr;</button>"));
                });
    }

    @Test
    public void searchOfferingsWithFilterTest() {
        webTestClient.get().uri("/items?search=+и+")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Настольная лампа"));
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Страница: 1"));

                    assertFalse(html.contains("Термокружка"));
                    assertFalse(html.contains("Зонт складной"));
                    assertFalse(html.contains("Рюкзак городской"));
                    assertFalse(html.contains("&larr;</button>"));
                    assertFalse(html.contains("&rarr;</button>"));
                });
    }

    @Test
    public void searchOfferingsWithSortingAndPaginationTest() {
        webTestClient.get().uri("/items?sort=PRICE&pageNumber=1&pageSize=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Страница: 1"));
                    assertFalse(html.contains("&larr;</button>"));
                    assertTrue(html.contains("&rarr;</button>"));
                    assertTrue(html.indexOf("990 руб.") < html.indexOf("1190 руб."));
                    assertTrue(html.indexOf("1190 руб.") < html.indexOf("1290 руб."));
                });
    }

    @Test
    public void searchOfferingsWithSortingAndPaginationTest_2page() {
        webTestClient.get().uri("/items?sort=PRICE&pageNumber=2&pageSize=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Страница: 2"));
                    assertTrue(html.contains("&larr;</button>"));
                    assertFalse(html.contains("&rarr;</button>"));
                    assertTrue(html.indexOf("1590 руб.") < html.indexOf("2790 руб."));
                });
    }

    @Test
    public void searchOfferingsTest_noResults() {
        webTestClient.get().uri("/items?search=INCORRECT_SEARCH")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                   assertNotNull(html);
                    assertTrue(html.contains("Страница: 1"));
                    assertFalse(html.contains("&larr;</button>"));
                    assertFalse(html.contains("&rarr;</button>"));
                    assertFalse(html.contains("card"));
                });
    }

    @ParameterizedTest
    @CsvSource({
            "NO, 0, 1",
            ", 1, 0",
            "PRICE, -1, 1",
            "ALPHA, 1, -1",
            "TEXT, 1, 5"
    })
    public void searchOfferingsTest_incorrectParams(String sort, String pageNumber, String pageSize) {
        webTestClient.get()
                .uri(String.format("/items?sort=%s&pageNumber=%s&pageSize=%s", sort, pageNumber, pageSize))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void getOfferingByIdTest_addedToCart() {
        webTestClient.get().uri("/items/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Беспроводная мышь"));
                    assertTrue(html.contains("Эргономичная мышь с Bluetooth-подключением и регулируемым DPI"));
                    assertTrue(html.contains("/wireless_mouse.jpg"));
                    assertTrue(html.contains("990 руб."));
                    assertTrue(html.contains("<span>2</span>"));
                    assertFalse(html.contains("button type=\"submit\" class=\"btn btn-warning ms-auto bi bi-cart4\" name=\"action\" value=\"PLUS\""));
                });
    }

    @Test
    public void getOfferingByIdTest_notInCart() {
        webTestClient.get().uri("/items/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertNotNull(html);
                    assertTrue(html.contains("Рюкзак городской"));
                    assertTrue(html.contains("Лёгкий водоотталкивающий рюкзак с отделением для ноутбука 15.6"));
                    assertTrue(html.contains("/backpack.jpg"));
                    assertTrue(html.contains("2790 руб."));
                    assertTrue(html.contains("<span>0</span>"));
                    assertTrue(html.contains("button type=\"submit\" class=\"btn btn-warning ms-auto bi bi-cart4\" name=\"action\" value=\"PLUS\""));
                });
    }

    @Test
    public void getOfferingByIdTest_nonExistingOffering() {
        webTestClient.get().uri("/items/300")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @ResetDB
    public void createNewOfferingTest() {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("title", "test");
        builder.part("description", "test description");
        builder.part("price", "1000");
        builder.part("image", "Test image content".getBytes())
                .header("Content-Disposition", "form-data; name=image; filename=NewBalance.jpg");

        webTestClient.post()
                .uri("/items/new")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}
