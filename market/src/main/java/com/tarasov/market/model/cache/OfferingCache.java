package com.tarasov.market.model.cache;


import com.tarasov.market.model.dto.OfferingDto;

import java.math.BigDecimal;


public record OfferingCache(
        Long id,
        String title,
        String description,
        String imgPath,
        BigDecimal price
) {

    public static OfferingCache from(OfferingDto offering) {
        return new OfferingCache(offering.id(),
                offering.title(),
                offering.description(),
                offering.imgPath(),
                offering.price());
    }
}
