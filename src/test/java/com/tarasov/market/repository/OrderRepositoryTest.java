package com.tarasov.market.repository;

import com.tarasov.market.MarketAppApplicationTest;
import com.tarasov.market.model.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryTest extends MarketAppApplicationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void getOrdersTest() {
        List<Order> orders = orderRepository.findAll();
        assertEquals(1, orders.size());
    }

    @Test
    @Transactional
    public void getOrderByIDTest() {
        Optional<Order> order = orderRepository.findById(1L);
        assertTrue(order.isPresent());
        assertEquals(BigDecimal.valueOf(1000), order.get().getTotalPrice());
        assertEquals(2, order.get().getOrderItems().size());
    }

    @Test
    public void getNonExistingOrderByIDTest() {
        Optional<Order> order = orderRepository.findById(100L);
        assertFalse(order.isPresent());
    }
}
