package com.tarasov.market.service.impl;

import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.OrderWithItem;
import com.tarasov.market.model.entity.Order;
import com.tarasov.market.model.entity.OrderItem;
import com.tarasov.market.model.dto.OrderDto;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OrderItemRepository;
import com.tarasov.market.repository.OrderRepository;
import com.tarasov.market.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Flux<OrderDto> getOrders() {
        return orderRepository.findAllWithItems()
                .groupBy(OrderWithItem::id)
                .flatMap(orderGroup ->
                    orderGroup.collectList().map(OrderDto::from)
                );
    }

    @Override
    public Mono<OrderDto> getOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .collectList()
                .map(OrderDto::from)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Order not found")));
    }

    @Override
    @Transactional
    public Mono<OrderDto> createOrderFromCart() {
        return cartRepository.findAllWithOffering()
                .switchIfEmpty(Mono.error(new IllegalStateException("The cart is empty")))
                .collectList()
                .flatMap(cartItems ->
                        createAndSaveOrder(cartItems)
                            .flatMap(order ->
                                    orderItemRepository.saveAll(convertCartItemsToOrderItems(cartItems, order))
                                            .collectList()
                                            .thenReturn(order))
                            .flatMap(order -> cartRepository.deleteAll().thenReturn(order))
                )
                .flatMap(order -> getOrderById(order.getId()));
    }

    private List<OrderItem> convertCartItemsToOrderItems(List<OfferingWithCartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrderId(order.getId());
                    orderItem.setOfferingId(cartItem.offeringId());
                    orderItem.setAmount(cartItem.amountInCart());
                    orderItem.setUnitPrice(cartItem.offeringPrice());
                    return orderItem;
                }).toList();
    }

    private Mono<Order> createAndSaveOrder(List<OfferingWithCartItem> cartItems) {
        Order order = new Order();
        BigDecimal totalPrice = calculateTotalPrice(cartItems);
        order.setTotalPrice(totalPrice);
        return orderRepository.save(order);
    }

    private BigDecimal calculateTotalPrice(List<OfferingWithCartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem ->
                        cartItem.offeringPrice()
                                .multiply(BigDecimal.valueOf(cartItem.amountInCart())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
