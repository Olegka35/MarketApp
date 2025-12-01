package com.tarasov.market.model.dto;

import com.tarasov.market.model.db.OrderWithItem;

import java.math.BigDecimal;
import java.util.List;

public record OrderDto(
        Long id,
        List<OrderItemDto> items,
        BigDecimal totalSum
) {
    public static OrderDto from(List<OrderWithItem> orderWithItemList) {
        return new OrderDto(orderWithItemList.getFirst().id(),
                orderWithItemList.stream().map(OrderItemDto::from).toList(),
                orderWithItemList.getFirst().totalPrice());
    }
}
