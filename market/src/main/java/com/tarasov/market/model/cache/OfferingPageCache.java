package com.tarasov.market.model.cache;


import com.tarasov.market.model.dto.OfferingPage;

import java.util.List;

public record OfferingPageCache(
        int totalPages,
        List<OfferingCache> offerings
) {
    public static OfferingPageCache from(OfferingPage offeringPage) {
        return new OfferingPageCache(offeringPage.getTotalPages(),
                offeringPage.getOfferings()
                        .stream()
                        .map(OfferingCache::from)
                        .toList()
        );
    }
}
