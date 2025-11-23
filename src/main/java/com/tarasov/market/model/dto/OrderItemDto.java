package com.tarasov.market.model.dto;

import com.tarasov.market.model.db.OrderWithItem;

import java.math.BigDecimal;

public record OrderItemDto(
        Long id,
        String title,
        BigDecimal price,
        int count
) {
    public static OrderItemDto from(OrderWithItem orderWithItem) {
        return new OrderItemDto(orderWithItem.orderItemId(),
                orderWithItem.offeringTitle(),
                orderWithItem.orderItemPrice(),
                orderWithItem.orderItemAmount());
    }
}
