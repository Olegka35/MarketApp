package com.tarasov.market.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponse {
    private List<CartItemDto> cartItems;
    private BigDecimal totalPrice;
}
