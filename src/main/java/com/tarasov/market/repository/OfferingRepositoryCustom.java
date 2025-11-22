package com.tarasov.market.repository;

import com.tarasov.market.model.dto.db.OfferingWithCartItem;
import com.tarasov.market.model.dto.db.PageRequest;
import reactor.core.publisher.Flux;

import java.util.Optional;

public interface OfferingRepositoryCustom {
    Flux<OfferingWithCartItem> findOfferings(PageRequest pageRequest, Optional<String> search);
}
