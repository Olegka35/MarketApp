package com.tarasov.market.service;


import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.Offering;
import com.tarasov.market.model.Order;
import com.tarasov.market.model.OrderItem;
import com.tarasov.market.model.dto.OrderDto;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OrderRepository;
import com.tarasov.market.service.impl.OrderServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Test
    public void getOrdersTest() {
        when(orderRepository.findAllWithItems()).thenReturn(generateTestOrders());

        List<OrderDto> orders = orderService.getOrders();

        assertEquals(2, orders.size());

        OrderDto order1 = orders.stream().filter(order -> order.id().equals(1L)).findFirst().orElseThrow();
        assertEquals(2, order1.items().size());
        assertEquals(BigDecimal.valueOf(900), order1.totalSum());
        assertEquals(BigDecimal.valueOf(200), order1.items().getFirst().price());
        assertEquals(4, order1.items().getFirst().count());
        assertEquals("Offering 1", order1.items().getFirst().title());
        assertEquals(BigDecimal.valueOf(100), order1.items().getLast().price());
        assertEquals(1, order1.items().getLast().count());
        assertEquals("Offering 2", order1.items().getLast().title());

        OrderDto order2 = orders.stream().filter(order -> order.id().equals(2L)).findFirst().orElseThrow();
        assertEquals(1, order2.items().size());
        assertEquals(BigDecimal.valueOf(200), order2.totalSum());
        assertEquals(BigDecimal.valueOf(100), order2.items().getFirst().price());
        assertEquals(2, order2.items().getFirst().count());
        assertEquals("Offering 2", order2.items().getFirst().title());
    }

    @Test
    public void getOrderByIdTest() {
        long id = 1L;
        Order testOrder = generateTestOrders().getFirst();
        when(orderRepository.findById(id)).thenReturn(Optional.of(testOrder));

        OrderDto order = orderService.getOrderById(id);

        assertEquals(2, order.items().size());
        assertEquals(BigDecimal.valueOf(900), order.totalSum());
        assertEquals(BigDecimal.valueOf(200), order.items().getFirst().price());
        assertEquals(4, order.items().getFirst().count());
        assertEquals("Offering 1", order.items().getFirst().title());
        assertEquals(BigDecimal.valueOf(100), order.items().getLast().price());
        assertEquals(1, order.items().getLast().count());
        assertEquals("Offering 2", order.items().getLast().title());
    }

    @Test
    public void getOrderByIdTest_notFound() {
        long id = 1L;
        when(orderRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(id));
    }

    @Test
    public void createOrderTest_emptyCart() {
        when(cartRepository.findAllWithOffering()).thenReturn(List.of());
        assertThrows(NoResultException.class, () -> orderService.createOrderFromCart());
    }

    @Test
    public void createOrderTest() {
        Offering offering1 = new Offering();
        offering1.setId(1L);
        offering1.setPrice(BigDecimal.valueOf(100));

        Offering offering2 = new Offering();
        offering2.setId(5L);
        offering2.setPrice(BigDecimal.valueOf(200));

        when(cartRepository.findAllWithOffering())
                .thenReturn(List.of(new CartItem(offering1, 1), new CartItem(offering2, 4)));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        orderService.createOrderFromCart();

        verify(cartRepository).deleteAll();

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order actualOrder = orderArgumentCaptor.getValue();
        assertEquals(BigDecimal.valueOf(900), actualOrder.getTotalPrice());
        assertEquals(2, actualOrder.getOrderItems().size());

        OrderItem orderItem1 = actualOrder.getOrderItems()
                .stream()
                .filter(oi -> oi.getOffering().getId().equals(1L))
                .findFirst().orElseThrow();
        assertEquals(BigDecimal.valueOf(100), orderItem1.getPrice());
        assertEquals(1, orderItem1.getAmount());

        OrderItem orderItem2 = actualOrder.getOrderItems()
                .stream()
                .filter(oi -> oi.getOffering().getId().equals(5L))
                .findFirst().orElseThrow();
        assertEquals(BigDecimal.valueOf(200), orderItem2.getPrice());
        assertEquals(4, orderItem2.getAmount());
    }

    private List<Order> generateTestOrders() {
        Offering offering1 = new Offering("Offering 1", "Test", "test", BigDecimal.valueOf(200));
        Offering offering2 = new Offering("Offering 2", "Test", "test", BigDecimal.valueOf(100));

        Order order1 = new Order();
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setOffering(offering1);
        orderItem1.setAmount(4);
        orderItem1.setPrice(BigDecimal.valueOf(200));

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setOffering(offering2);
        orderItem2.setAmount(1);
        orderItem2.setPrice(BigDecimal.valueOf(100));

        order1.setId(1L);
        order1.setTotalPrice(BigDecimal.valueOf(900));
        order1.setOrderItems(List.of(orderItem1, orderItem2));

        Order order2 = new Order();
        OrderItem orderItem3 = new OrderItem();
        orderItem3.setOffering(offering2);
        orderItem3.setAmount(2);
        orderItem3.setPrice(BigDecimal.valueOf(100));

        order2.setId(2L);
        order2.setTotalPrice(BigDecimal.valueOf(200));
        order2.setOrderItems(List.of(orderItem3));

        return List.of(order1, order2);
    }
}
