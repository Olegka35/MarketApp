package com.tarasov.market.service;


import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.OrderWithItem;
import com.tarasov.market.model.entity.Order;
import com.tarasov.market.model.entity.OrderItem;
import com.tarasov.market.model.dto.OrderDto;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OrderItemRepository;
import com.tarasov.market.repository.OrderRepository;
import com.tarasov.market.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentService paymentService;

    @Test
    public void getOrdersTest() {
        when(orderRepository.findAllWithItems()).thenReturn(generateTestOrders());

        List<OrderDto> orders = orderService.getOrders().collectList().block();

        assertNotNull(orders);
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
        when(orderRepository.findByIdWithItems(id))
                .thenReturn(generateTestOrders()
                        .filter(order -> order.id().equals(id)));

        OrderDto order = orderService.getOrderById(id).block();

        assertNotNull(order);
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
        when(orderRepository.findByIdWithItems(id)).thenReturn(Flux.empty());
        assertThrows(NoSuchElementException.class, () -> orderService.getOrderById(id).block());
    }

    @Test
    public void createOrderTest_emptyCart() {
        when(cartRepository.findAllWithOffering()).thenReturn(Flux.empty());
        assertThrows(IllegalStateException.class, () -> orderService.createOrderFromCart().block());
    }

    @Test
    public void createOrderTest() {
        OfferingWithCartItem item1 = new OfferingWithCartItem(1L,
                "Test", "Test", "test.png", BigDecimal.valueOf(100),
                10L, 1);

        OfferingWithCartItem item2 = new OfferingWithCartItem(5L,
                "Test2", "Test2", "test2.png", BigDecimal.valueOf(200),
                11L, 4);

        when(cartRepository.findAllWithOffering())
                .thenReturn(Flux.just(item1, item2));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order order = invocation.getArgument(0);
                    order.setId(1L);
                    return Mono.just(order);
                });
        when(orderItemRepository.saveAll(anyList())).thenReturn(Flux.just());
        when(cartRepository.deleteAll()).thenReturn(Mono.empty().then());
        when(orderRepository.findByIdWithItems(1L))
                .thenReturn(generateTestOrders().filter(order -> order.id().equals(1L)));
        when(paymentService.makePayment(any())).thenReturn(Mono.just(BigDecimal.TEN));

        orderService.createOrderFromCart().block();

        verify(cartRepository).deleteAll();

        ArgumentCaptor<Order> orderArgumentCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderArgumentCaptor.capture());
        Order savedOrder = orderArgumentCaptor.getValue();
        assertEquals(BigDecimal.valueOf(900), savedOrder.getTotalPrice());

        verify(paymentService).makePayment(BigDecimal.valueOf(900));

        ArgumentCaptor<List<OrderItem>> orderItemsCaptor = ArgumentCaptor.forClass(List.class);
        verify(orderItemRepository).saveAll(orderItemsCaptor.capture());
        List<OrderItem> savedOrderItems = orderItemsCaptor.getValue();
        assertEquals(2, savedOrderItems.size());

        OrderItem orderItem1 = savedOrderItems
                .stream()
                .filter(oi -> oi.getOfferingId().equals(1L))
                .findFirst().orElseThrow();
        assertEquals(BigDecimal.valueOf(100), orderItem1.getUnitPrice());
        assertEquals(1, orderItem1.getAmount());

        OrderItem orderItem2 = savedOrderItems
                .stream()
                .filter(oi -> oi.getOfferingId().equals(5L))
                .findFirst().orElseThrow();
        assertEquals(BigDecimal.valueOf(200), orderItem2.getUnitPrice());
        assertEquals(4, orderItem2.getAmount());
    }

    private Flux<OrderWithItem> generateTestOrders() {
        OrderWithItem item1 = new OrderWithItem(1L, BigDecimal.valueOf(900),
                1L, BigDecimal.valueOf(200), 4, "Offering 1");
        OrderWithItem item2 = new OrderWithItem(1L, BigDecimal.valueOf(900),
                2L, BigDecimal.valueOf(100), 1, "Offering 2");
        OrderWithItem item3 = new OrderWithItem(2L, BigDecimal.valueOf(200),
                3L, BigDecimal.valueOf(100), 2, "Offering 2");
        return Flux.just(item1, item2, item3);
    }
}
