package com.tarasov.market.repository;

import com.tarasov.market.model.cache.OfferingCache;
import com.tarasov.market.model.cache.OfferingPageCache;
import com.tarasov.market.model.type.SortType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OfferingCacheRepositoryTest extends BaseRepositoryTest {

    @Autowired
    OfferingCacheRepository offeringCacheRepository;

    @Test
    public void saveOfferingInCacheTest() {
        OfferingCache expectedCache
                = new OfferingCache(2L, "Sneakers", "New Balance", "new_balance.img", BigDecimal.valueOf(200));

        offeringCacheRepository.saveOffering(expectedCache).block();

        OfferingCache offeringCache = offeringCacheRepository.findByOfferingId(2L).block();

        assertEquals(expectedCache, offeringCache);
    }

    @Test
    public void saveOfferingPageInCacheTest() {
        OfferingPageCache expectedPage = new OfferingPageCache(2,
                List.of(
                        new OfferingCache(2L, "Sneakers", "New Balance", "new_balance.img", BigDecimal.valueOf(200)),
                        new OfferingCache(3L, "Test offering", "Description", "image.img", BigDecimal.valueOf(500))
                )
        );
        offeringCacheRepository.saveOfferingPage("", SortType.PRICE, 1, 2, expectedPage).block();

        OfferingPageCache offeringPageCache
                = offeringCacheRepository.findOfferingPage("", SortType.PRICE, 1, 2).block();

        assertEquals(expectedPage, offeringPageCache);
    }

    @Test
    public void clearCacheTest() {
        OfferingPageCache expectedPage = new OfferingPageCache(2,
                List.of(
                        new OfferingCache(2L, "Sneakers", "New Balance", "new_balance.img", BigDecimal.valueOf(200)),
                        new OfferingCache(3L, "Test offering", "Description", "image.img", BigDecimal.valueOf(500))
                )
        );
        offeringCacheRepository.saveOfferingPage("", SortType.PRICE, 1, 2, expectedPage).block();

        offeringCacheRepository.clearCache().block();

        OfferingPageCache offeringPageCache
                = offeringCacheRepository.findOfferingPage("", SortType.PRICE, 1, 2).block();
        assertNull(offeringPageCache);
    }
}
