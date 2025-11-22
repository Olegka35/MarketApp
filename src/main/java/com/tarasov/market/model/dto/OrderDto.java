package com.tarasov.market.model.dto;

import com.tarasov.market.model.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        List<OrderItemDto> items,
        BigDecimal totalSum
) {
    public static OrderDto from(Order order) {
        return new OrderDto(order.getId(),
                order.getOrderItems().stream().map(OrderItemDto::from).toList(),
                order.getTotalPrice());
    }
}
