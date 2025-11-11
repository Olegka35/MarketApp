package com.tarasov.market.model.dto;

import com.tarasov.market.model.Offering;

import java.math.BigDecimal;

public record OfferingDto(
        long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price,
        int count
) {
    public static OfferingDto from(Offering offering) {
        int cartCount = offering.getCartItem() == null ? 0 : offering.getCartItem().getAmount();
        return new OfferingDto(offering.getId(),
                offering.getTitle(),
                offering.getDescription(),
                offering.getImgPath(),
                offering.getPrice(),
                cartCount);
    }

    public static OfferingDto dummyOffering() {
        return new OfferingDto(-1L, null, null, null, null, 0);
    }
}
