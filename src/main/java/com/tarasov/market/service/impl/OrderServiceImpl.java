package com.tarasov.market.service.impl;

import com.tarasov.market.model.Order;
import com.tarasov.market.model.dto.OrderDto;
import com.tarasov.market.repository.OrderItemRepository;
import com.tarasov.market.repository.OrderRepository;
import com.tarasov.market.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public List<OrderDto> getOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(OrderDto::from).toList();
    }

    @Override
    public OrderDto getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(OrderDto::from)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
    }
}
