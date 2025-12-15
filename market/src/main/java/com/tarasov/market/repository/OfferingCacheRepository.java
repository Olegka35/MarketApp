package com.tarasov.market.repository;

import com.tarasov.market.model.cache.OfferingCache;
import com.tarasov.market.model.cache.OfferingPageCache;
import com.tarasov.market.model.type.SortType;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface OfferingCacheRepository {

    Mono<Boolean> saveOffering(OfferingCache offeringCache);
    Mono<OfferingCache> findByOfferingId(Long offeringId);

    Mono<Boolean> saveOfferingPage(String search,
                                   SortType sortType,
                                   int pageNumber,
                                   int pageSize,
                                   OfferingPageCache offeringPageCache);

    Mono<OfferingPageCache> findOfferingPage(String search,
                                             SortType sortType,
                                             int pageNumber,
                                             int pageSize);
    Mono<Long> clearCache();
}
