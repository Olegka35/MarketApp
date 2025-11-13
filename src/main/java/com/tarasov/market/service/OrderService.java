package com.tarasov.market.service;

import com.tarasov.market.model.dto.OrderDto;

import java.util.List;

public interface OrderService {
    List<OrderDto> getOrders();
    OrderDto getOrderById(Long id);
}
