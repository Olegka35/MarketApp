package com.tarasov.market.repository;

import com.tarasov.market.configuration.ResetDB;
import com.tarasov.market.model.db.OrderWithItem;
import com.tarasov.market.model.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    public void getOrdersTest() {
        List<OrderWithItem> orderItems = orderRepository.findAllWithItems().collectList().block();

        assertNotNull(orderItems);
        assertEquals(2, orderItems.size());
        assertEquals(1, orderItems.stream().map(OrderWithItem::id).distinct().count());
        assertEquals(1L, orderItems.getFirst().id());
        assertEquals(BigDecimal.valueOf(1000), orderItems.getFirst().totalPrice());
    }

    @Test
    @ResetDB
    public void getOrderByIDTest() {
        List<OrderWithItem> orderItems = orderRepository.findByIdWithItems(1L).collectList().block();

        assertNotNull(orderItems);
        assertEquals(BigDecimal.valueOf(1000), orderItems.getFirst().totalPrice());
        assertEquals(2, orderItems.size());
    }

    @Test
    public void getNonExistingOrderByIDTest() {
        List<OrderWithItem> orderItems = orderRepository.findByIdWithItems(100L).collectList().block();
        assertNotNull(orderItems);
        assertTrue(orderItems.isEmpty());
    }

    @Test
    @ResetDB
    public void createOrderTest() {
        Order order = new Order();
        order.setTotalPrice(BigDecimal.valueOf(200));

        Order newOrder = orderRepository.save(order)
                .flatMap(createdOrder -> orderRepository.findById(createdOrder.getId()))
                .block();

        assertNotNull(newOrder);
        assertEquals(BigDecimal.valueOf(200), newOrder.getTotalPrice());
    }

    /*private OrderItem generateOrderItem(Order order) {
        Offering offering = new Offering();
        offering.setId(1L);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setPrice(BigDecimal.valueOf(100));
        orderItem.setOffering(offering);
        orderItem.setAmount(1);
        return orderItem;
    }*/
}
