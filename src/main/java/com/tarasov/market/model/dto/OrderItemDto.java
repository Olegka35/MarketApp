package com.tarasov.market.model.dto;

import com.tarasov.market.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        String title,
        BigDecimal price,
        int count
) {
    public static OrderItemDto from(OrderItem orderItem) {
        return new OrderItemDto(orderItem.getId(),
                orderItem.getOffering().getTitle(),
                orderItem.getOffering().getPrice(),
                orderItem.getAmount());
    }
}
