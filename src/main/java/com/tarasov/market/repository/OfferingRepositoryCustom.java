package com.tarasov.market.repository;

import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.PageRequest;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface OfferingRepositoryCustom {
    Flux<OfferingWithCartItem> findOfferings(PageRequest pageRequest, Optional<String> search);
}
