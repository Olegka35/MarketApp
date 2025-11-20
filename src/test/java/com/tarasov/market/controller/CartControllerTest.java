package com.tarasov.market.controller;

import com.tarasov.market.model.dto.CartItemDto;
import com.tarasov.market.model.dto.OfferingDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerTest extends BaseControllerTest {

    @Test
    public void getCartItemsTest() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(result -> {
                    BigDecimal totalPrice = (BigDecimal) result.getModelAndView().getModel().get("total");
                    assertEquals(BigDecimal.valueOf(3170), totalPrice);

                    List<CartItemDto> cartItems = (List<CartItemDto>) result.getModelAndView().getModel().get("items");
                    assertEquals(2, cartItems.size());

                    CartItemDto mouseItem = cartItems.stream()
                            .filter(item -> item.id() == 2L)
                            .findFirst().orElseThrow();
                    assertEquals(2, mouseItem.count());
                    assertEquals(BigDecimal.valueOf(990), mouseItem.price());
                    assertEquals("Беспроводная мышь", mouseItem.title());
                    assertEquals("/wireless_mouse.jpg", mouseItem.imgPath());

                    CartItemDto umbrellaItem = cartItems.stream()
                            .filter(item -> item.id() == 5L)
                            .findFirst().orElseThrow();
                    assertEquals(1, umbrellaItem.count());
                    assertEquals(BigDecimal.valueOf(1190), umbrellaItem.price());
                    assertEquals("Зонт складной", umbrellaItem.title());
                    assertEquals("/umbrella.jpg", umbrellaItem.imgPath());
                });
    }

    @Test
    @Transactional
    public void addCartItemFromCartPageTest() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", "PLUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(result -> {
                    BigDecimal totalPrice = (BigDecimal) result.getModelAndView().getModel().get("total");
                    assertEquals(BigDecimal.valueOf(4460), totalPrice);

                    List<CartItemDto> cartItems = (List<CartItemDto>) result.getModelAndView().getModel().get("items");
                    assertEquals(3, cartItems.size());

                    CartItemDto newItem = cartItems.stream()
                            .filter(item -> item.id() == 1L)
                            .findFirst().orElseThrow();
                    assertEquals(1, newItem.count());
                    assertEquals(BigDecimal.valueOf(1290), newItem.price());
                    assertEquals("Термокружка 500 мл", newItem.title());
                    assertEquals("/thermocup.jpg", newItem.imgPath());
                });
    }

    @Test
    @Transactional
    public void removeCartItemFromCartPageTest() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", "2")
                        .param("action", "MINUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(result -> {
                    BigDecimal totalPrice = (BigDecimal) result.getModelAndView().getModel().get("total");
                    assertEquals(BigDecimal.valueOf(2180), totalPrice);

                    List<CartItemDto> cartItems = (List<CartItemDto>) result.getModelAndView().getModel().get("items");
                    assertEquals(2, cartItems.size());

                    CartItemDto cartItem = cartItems.stream()
                            .filter(item -> item.id() == 2L)
                            .findFirst().orElseThrow();
                    assertEquals(1, cartItem.count());
                    assertEquals(BigDecimal.valueOf(990), cartItem.price());
                    assertEquals("Беспроводная мышь", cartItem.title());
                    assertEquals("/wireless_mouse.jpg", cartItem.imgPath());
                });
    }

    @Test
    @Transactional
    public void removeCartItemToZeroFromCartPageTest() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", "5")
                        .param("action", "MINUS"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(result -> {
                    BigDecimal totalPrice = (BigDecimal) result.getModelAndView().getModel().get("total");
                    assertEquals(BigDecimal.valueOf(1980), totalPrice);

                    List<CartItemDto> cartItems = (List<CartItemDto>) result.getModelAndView().getModel().get("items");
                    assertEquals(1, cartItems.size());

                    assertTrue(cartItems.stream().noneMatch(item -> item.id() == 5));
                });
    }

    @Test
    @Transactional
    public void deleteCartItemFromCartPageTest() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", "2")
                        .param("action", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(result -> {
                    BigDecimal totalPrice = (BigDecimal) result.getModelAndView().getModel().get("total");
                    assertEquals(BigDecimal.valueOf(1190), totalPrice);

                    List<CartItemDto> cartItems = (List<CartItemDto>) result.getModelAndView().getModel().get("items");
                    assertEquals(1, cartItems.size());

                    assertTrue(cartItems.stream().noneMatch(item -> item.id() == 2));
                });
    }

    @Test
    @Transactional
    public void deleteCartItemFromCartPageTest_notExistInCart() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", "1")
                        .param("action", "DELETE"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void addCartItemFromCartPageTest_incorrectOfferingId() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", "200")
                        .param("action", "PLUS"))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "0, PLUS",
            "-1, MINUS",
            "2, ",
            "5, ADD",
            "5, REMOVE"
    })
    @Transactional
    public void updateCartItemFromCartPageTest_badRequest(String id,
                                                          String action) throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("id", id)
                        .param("action", action))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "1, , , , 3, PLUS",
            "2, , , 2, 3, MINUS",
            "5, , , , , MINUS",
            "5, Зонт, PRICE, 1, 5, PLUS",
            "5, , , , , DELETE"
    })
    @Transactional
    public void updateCartItemFromMainPageTest(String id,
                                               String search,
                                               String sort,
                                               String pageNumber,
                                               String pageSize,
                                               String action) throws Exception {
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(
                        String.format("/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                                Optional.ofNullable(search).orElse(""),
                                Optional.ofNullable(sort).orElse("NO"),
                                Integer.valueOf(Optional.ofNullable(pageNumber).orElse("1")),
                                Integer.valueOf(Optional.ofNullable(pageSize).orElse("5")))
                        )
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
    @Transactional
    public void updateCartItemFromMainPageTest_badRequest(String id,
                                               String search,
                                               String sort,
                                               String pageNumber,
                                               String pageSize,
                                               String action) throws Exception {
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize)
                        .param("action", action))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "100, , , , 3, PLUS",
            "4, , , 2, 3, MINUS",
            "1, , , , , DELETE"
    })
    @Transactional
    public void updateCartItemFromMainPageTest_notFound(String id,
                                                          String search,
                                                          String sort,
                                                          String pageNumber,
                                                          String pageSize,
                                                          String action) throws Exception {
        mockMvc.perform(post("/items")
                        .param("id", id)
                        .param("search", search)
                        .param("sort", sort)
                        .param("pageNumber", pageNumber)
                        .param("pageSize", pageSize)
                        .param("action", action))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @CsvSource({
            "1, PLUS, 1, 1290, Термокружка 500 мл, /thermocup.jpg",
            "5, PLUS, 2, 1190, Зонт складной, /umbrella.jpg",
            "2, MINUS, 1, 990, Беспроводная мышь, /wireless_mouse.jpg",
            "2, DELETE, 0, 990, Беспроводная мышь, /wireless_mouse.jpg",
            "5, DELETE, 0, 1190, Зонт складной, /umbrella.jpg",
    })
    @Transactional
    public void updateCartItemFromOfferingPageTest(String id,
                                                   String action,
                                                   String newCount,
                                                   String price,
                                                   String title,
                                                   String imgPath) throws Exception {
        mockMvc.perform(post("/items/" + id)
                        .param("action", action))
                .andExpect(status().isOk())
                .andExpect(view().name("item"))
                .andExpect(result -> {
                    OfferingDto offering = (OfferingDto) result.getModelAndView().getModel().get("item");
                    assertEquals(Long.valueOf(id), offering.id());
                    assertEquals(Integer.valueOf(newCount), offering.count());
                    assertEquals(new BigDecimal(price), offering.price());
                    assertEquals(title, offering.title());
                    assertEquals(imgPath, offering.imgPath());
                });
    }

    @ParameterizedTest
    @CsvSource({
            "0, PLUS",
            "5, ADD",
            "-1, MINUS",
            "2, REMOVE"
    })
    @Transactional
    public void updateCartItemFromOfferingPageTest_badRequest(String id,
                                                              String action) throws Exception {
        mockMvc.perform(post("/items/" + id)
                        .param("action", action))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @CsvSource({
            "100, PLUS",
            "1, DELETE",
            "1, MINUS"
    })
    @Transactional
    public void updateCartItemFromOfferingPageTest_notFound(String id,
                                                              String action) throws Exception {
        mockMvc.perform(post("/items/" + id)
                        .param("action", action))
                .andExpect(status().isNotFound());
    }
}
