package com.tarasov.market.model.dto;

import com.tarasov.market.model.CartItem;

import java.math.BigDecimal;

public record CartItemDto(
        long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price,
        int count
) {
    public static CartItemDto from(CartItem cartItem) {
        return new CartItemDto(cartItem.getOffering().getId(),
                cartItem.getOffering().getTitle(),
                cartItem.getOffering().getDescription(),
                cartItem.getOffering().getImgPath(),
                cartItem.getOffering().getPrice(),
                cartItem.getAmount());
    }
}
