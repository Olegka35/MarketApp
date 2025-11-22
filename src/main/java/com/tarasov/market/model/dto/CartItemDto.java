package com.tarasov.market.model.dto;

import com.tarasov.market.model.dto.db.OfferingWithCartItem;

import java.math.BigDecimal;

public record CartItemDto(
        long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price,
        int count
) {
    public static CartItemDto from(OfferingWithCartItem offeringWithCartItem) {
        return new CartItemDto(offeringWithCartItem.offeringId(),
                offeringWithCartItem.offeringTitle(),
                offeringWithCartItem.offeringDescription(),
                offeringWithCartItem.offeringImgPath(),
                offeringWithCartItem.offeringPrice(),
                offeringWithCartItem.amountInCart());
    }
}
