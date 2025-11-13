package com.tarasov.market.repository;

import com.tarasov.market.MarketAppApplicationTest;
import com.tarasov.market.model.Offering;
import com.tarasov.market.model.Order;
import com.tarasov.market.model.OrderItem;
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

    @Test
    @Transactional
    public void createOrderTest() {
        Order order = new Order();
        List<OrderItem> orderItems = List.of(generateOrderItem(order),  generateOrderItem(order));
        order.setTotalPrice(BigDecimal.valueOf(200));
        order.setOrderItems(orderItems);
        order = orderRepository.save(order);

        Optional<Order> createdOrder = orderRepository.findById(order.getId());
        assertTrue(createdOrder.isPresent());
        assertEquals(BigDecimal.valueOf(200), createdOrder.get().getTotalPrice());
        assertEquals(2, createdOrder.get().getOrderItems().size());
    }

    private OrderItem generateOrderItem(Order order) {
        Offering offering = new Offering();
        offering.setId(1L);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setPrice(BigDecimal.valueOf(100));
        orderItem.setOffering(offering);
        orderItem.setAmount(1);
        return orderItem;
    }
}
