package com.tarasov.market.model.db;

import java.math.BigDecimal;

public record OrderWithItem(
        Long id,
        BigDecimal totalPrice,
        Long orderItemId,
        BigDecimal orderItemPrice,
        Integer orderItemAmount,
        String offeringTitle
) {
}
