package com.tarasov.market.controller;

import com.tarasov.market.model.dto.OrderDto;
import com.tarasov.market.model.dto.OrderItemDto;
import com.tarasov.market.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class OrderControllerTest extends BaseControllerTest {

    @Autowired
    CartRepository cartRepository;

    @Test
    @Transactional
    public void createNewOrderTest() throws Exception {
        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    public void createNewOrderTest_emptyCart() throws Exception {
        cartRepository.findAll().forEach(cartItem -> cartItem.getOffering().setCartItem(null));
        cartRepository.deleteAll();
        mockMvc.perform(post("/buy"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getOrdersTest() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("orders"))
                .andExpect(result -> {
                    List<OrderDto> orders = (List<OrderDto>) result.getModelAndView().getModel().get("orders");
                    assertEquals(1, orders.size());
                    assertEquals(BigDecimal.valueOf(1000), orders.getFirst().totalSum());
                    assertEquals(2, orders.getFirst().items().size());

                    OrderItemDto mouseItem = orders.getFirst().items().stream()
                            .filter(item -> item.title().equals("Беспроводная мышь"))
                            .findFirst().orElseThrow();
                    assertEquals(2, mouseItem.count());
                    assertEquals(BigDecimal.valueOf(400), mouseItem.price());
                    assertEquals("Беспроводная мышь", mouseItem.title());

                    OrderItemDto backpackItem = orders.getFirst().items().stream()
                            .filter(item -> item.title().equals("Рюкзак городской"))
                            .findFirst().orElseThrow();
                    assertEquals(1, backpackItem.count());
                    assertEquals(BigDecimal.valueOf(200), backpackItem.price());
                    assertEquals("Рюкзак городской", backpackItem.title());
                });
    }

    @Test
    public void getOrderByIdTest() throws Exception {
        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attribute("newOrder", false))
                .andExpect(result -> {
                    OrderDto order = (OrderDto) result.getModelAndView().getModel().get("order");
                    assertEquals(BigDecimal.valueOf(1000), order.totalSum());
                    assertEquals(2, order.items().size());

                    OrderItemDto mouseItem = order.items().stream()
                            .filter(item -> item.title().equals("Беспроводная мышь"))
                            .findFirst().orElseThrow();
                    assertEquals(2, mouseItem.count());
                    assertEquals(BigDecimal.valueOf(400), mouseItem.price());
                    assertEquals("Беспроводная мышь", mouseItem.title());

                    OrderItemDto backpackItem = order.items().stream()
                            .filter(item -> item.title().equals("Рюкзак городской"))
                            .findFirst().orElseThrow();
                    assertEquals(1, backpackItem.count());
                    assertEquals(BigDecimal.valueOf(200), backpackItem.price());
                    assertEquals("Рюкзак городской", backpackItem.title());
                });
    }

    @Test
    public void getOrderByIdAfterCreationTest() throws Exception {
        mockMvc.perform(get("/orders/1?newOrder=true"))
                .andExpect(status().isOk())
                .andExpect(view().name("order"))
                .andExpect(model().attribute("newOrder", true))
                .andExpect(result -> {
                    OrderDto order = (OrderDto) result.getModelAndView().getModel().get("order");
                    assertEquals(BigDecimal.valueOf(1000), order.totalSum());
                    assertEquals(2, order.items().size());

                    OrderItemDto mouseItem = order.items().stream()
                            .filter(item -> item.title().equals("Беспроводная мышь"))
                            .findFirst().orElseThrow();
                    assertEquals(2, mouseItem.count());
                    assertEquals(BigDecimal.valueOf(400), mouseItem.price());
                    assertEquals("Беспроводная мышь", mouseItem.title());

                    OrderItemDto backpackItem = order.items().stream()
                            .filter(item -> item.title().equals("Рюкзак городской"))
                            .findFirst().orElseThrow();
                    assertEquals(1, backpackItem.count());
                    assertEquals(BigDecimal.valueOf(200), backpackItem.price());
                    assertEquals("Рюкзак городской", backpackItem.title());
                });
    }

    @Test
    public void getOrderByIdTest_incorrectId() throws Exception {
        mockMvc.perform(get("/orders/0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getOrderByIdTest_orderNotExist() throws Exception {
        mockMvc.perform(get("/orders/5"))
                .andExpect(status().isNotFound());
    }
}
