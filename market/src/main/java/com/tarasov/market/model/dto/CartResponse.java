package com.tarasov.market.model.dto;


import com.tarasov.market.model.type.PaymentError;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartResponse {
    private List<CartItemDto> cartItems;
    private BigDecimal totalPrice;
    private PaymentError error;

    public CartResponse(List<CartItemDto> cartItems, BigDecimal totalPrice) {
        this.cartItems = cartItems;
        this.totalPrice = totalPrice;
    }
}
