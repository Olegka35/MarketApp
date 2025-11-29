package com.tarasov.market.model.dto;

import com.tarasov.market.model.db.OfferingWithCartItem;

import java.math.BigDecimal;

public record OfferingDto(
        long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price,
        int count
) {
    public static OfferingDto from(OfferingWithCartItem offeringWithCartItem) {
        int cartCount = offeringWithCartItem.amountInCart() == null ? 0 : offeringWithCartItem.amountInCart();
        return new OfferingDto(offeringWithCartItem.offeringId(),
                offeringWithCartItem.offeringTitle(),
                offeringWithCartItem.offeringDescription(),
                offeringWithCartItem.offeringImgPath(),
                offeringWithCartItem.offeringPrice(),
                cartCount);
    }

    public static OfferingDto dummyOffering() {
        return new OfferingDto(-1L, null, null, null, null, 0);
    }
}
