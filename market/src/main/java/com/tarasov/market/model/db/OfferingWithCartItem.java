package com.tarasov.market.model.db;

import java.math.BigDecimal;

public record OfferingWithCartItem(
        Long offeringId,
        String offeringTitle,
        String offeringDescription,
        String offeringImgPath,
        BigDecimal offeringPrice,
        Long cartItemId,
        Integer amountInCart
) {
}
