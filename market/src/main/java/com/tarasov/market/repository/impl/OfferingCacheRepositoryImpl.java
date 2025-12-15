package com.tarasov.market.repository.impl;

import com.tarasov.market.model.cache.OfferingCache;
import com.tarasov.market.model.cache.OfferingPageCache;
import com.tarasov.market.model.type.SortType;
import com.tarasov.market.repository.OfferingCacheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
public class OfferingCacheRepositoryImpl implements OfferingCacheRepository {

    private static final String OFFERING_KEY_SPACE = "offering:";
    private static final String OFFERING_PAGE_KEY_SPACE = "offering:page:";

    private final ReactiveRedisTemplate<String, OfferingCache> offeringCacheRedisTemplate;
    private final ReactiveRedisTemplate<String, OfferingPageCache> offeringPageCacheRedisTemplate;
    private final int cacheTime;

    public OfferingCacheRepositoryImpl(ReactiveRedisTemplate<String, OfferingCache> offeringCacheRedisTemplate,
                                       ReactiveRedisTemplate<String, OfferingPageCache> offeringPageCacheRedisTemplate,
                                       @Value("${offering.cache-time}") int cacheTime) {
        this.offeringCacheRedisTemplate = offeringCacheRedisTemplate;
        this.offeringPageCacheRedisTemplate = offeringPageCacheRedisTemplate;
        this.cacheTime = cacheTime;
    }

    @Override
    public Mono<Boolean> saveOffering(OfferingCache offeringCache) {
        return offeringCacheRedisTemplate.opsForValue()
                .set(OFFERING_KEY_SPACE + offeringCache.id(),
                        offeringCache, Duration.ofMinutes(cacheTime));
    }

    @Override
    public Mono<OfferingCache> findByOfferingId(Long offeringId) {
        return offeringCacheRedisTemplate.opsForValue()
                .get(OFFERING_KEY_SPACE + offeringId);
    }

    @Override
    public Mono<Boolean> saveOfferingPage(String search, SortType sortType, int pageNumber, int pageSize, OfferingPageCache offeringPageCache) {
        return offeringPageCacheRedisTemplate.opsForValue()
                .set(getRedisSpaceKey(search, sortType, pageNumber, pageSize),
                        offeringPageCache, Duration.ofMinutes(cacheTime));
    }

    @Override
    public Mono<OfferingPageCache> findOfferingPage(String search, SortType sortType, int pageNumber, int pageSize) {
        return offeringPageCacheRedisTemplate.opsForValue()
                .get(getRedisSpaceKey(search, sortType, pageNumber, pageSize));
    }

    @Override
    public Mono<Long> clearCache() {
        return offeringPageCacheRedisTemplate
                .delete(offeringPageCacheRedisTemplate.keys("offering:page:*"));
    }

    private String getRedisSpaceKey(String search, SortType sortType, int pageNumber, int pageSize) {
        return OFFERING_PAGE_KEY_SPACE
                + "sort:" + sortType.name()
                + ":number:" + pageNumber
                + ":size:" + pageSize
                + (search.isBlank() ? "" : (":search:" + search));
    }
}
