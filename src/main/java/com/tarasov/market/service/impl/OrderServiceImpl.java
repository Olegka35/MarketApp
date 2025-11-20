package com.tarasov.market.service.impl;

import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.Order;
import com.tarasov.market.model.OrderItem;
import com.tarasov.market.model.dto.OrderDto;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OrderRepository;
import com.tarasov.market.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    @Override
    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository.findAllWithItems();
        return orders.stream().map(OrderDto::from).toList();
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }

    @Override
    @Transactional
    public OrderDto createOrderFromCart() {
        List<CartItem> cartItems = cartRepository.findAllWithOffering();
        if (cartItems.isEmpty()) {
            throw new NoResultException("The cart is empty");
        }
        Order order = new Order();
        List<OrderItem> orderItems = convertCartItemsToOrderItems(cartItems, order);
        BigDecimal totalPrice = calculateTotalPrice(orderItems);
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);
        cartItems.forEach(cartItem -> cartItem.getOffering().setCartItem(null));
        cartRepository.deleteAll();
        return OrderDto.from(orderRepository.save(order));
    }

    private List<OrderItem> convertCartItemsToOrderItems(List<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOffering(cartItem.getOffering());
                    orderItem.setAmount(cartItem.getAmount());
                    orderItem.setPrice(cartItem.getOffering().getPrice());
                    orderItem.setOrder(order);
                    return orderItem;
                }).toList();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(oi -> oi.getPrice().multiply(BigDecimal.valueOf(oi.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
