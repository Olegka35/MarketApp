package com.tarasov.market.repository;

import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.PageRequest;
import reactor.core.publisher.Flux;


public interface OfferingRepositoryCustom {
    Flux<OfferingWithCartItem> findOfferings(PageRequest pageRequest, String search);
}
