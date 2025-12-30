package com.tarasov.market.repository;

import com.tarasov.market.configuration.ResetDB;
import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.db.PageRequest;
import com.tarasov.market.model.entity.Offering;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class OfferingRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OfferingRepository offeringRepository;

    @Test
    public void findAllOfferingsTest() {
        assertThat(offeringRepository.findAllWithCart().collectList().block()).hasSize(5);
    }

    @Test
    public void findOfferingsWithPriceSortingTest() {
        List<OfferingWithCartItem> offerings =
                offeringRepository.findOfferings(new PageRequest(1, 10, "offering_price"), null)
                        .collectList()
                        .block();

        assertThat(offerings).hasSize(5);
        assertThat(offerings.getFirst().offeringPrice()).isEqualTo(new BigDecimal("990"));
        assertThat(offerings.getLast().offeringPrice()).isEqualTo(new BigDecimal("2790"));
    }

    @Test
    public void findOfferingsWithPriceSortingAndPaginationTest() {
        List<OfferingWithCartItem> offerings = offeringRepository
                .findOfferings(new PageRequest(2, 3, "offering_price"), null)
                .collectList()
                .block();
        assertThat(offerings).hasSize(2);
    }

    @Test
    public void findOfferingsWithNameSortingTest() {
        List<OfferingWithCartItem> offerings = offeringRepository
                .findOfferings(new PageRequest(1, 10, "offering_title"), null)
                .collectList()
                .block();
        assertThat(offerings).hasSize(5);
        assertThat(offerings.getFirst().offeringTitle()).isEqualTo("Беспроводная мышь");
        assertThat(offerings.getLast().offeringTitle()).isEqualTo("Термокружка 500 мл");
    }

    @Test
    public void countOfferingsByTitleTest() {
        Integer count = offeringRepository.countByTitleContainingOrDescriptionContaining(" и ", " и ")
                .block();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void searchOfferingsByTitleTest() {
        List<OfferingWithCartItem> offerings = offeringRepository
                .findOfferings(new PageRequest(1, 10), " и ")
                .collectList()
                .block();
        assertThat(offerings).hasSize(2);
    }

    @Test
    @ResetDB
    public void addOfferingTest() {
        Offering offering = offeringRepository.save(
                new Offering("New product", "Description", "product.img", BigDecimal.valueOf(50000))
        ).block();

        assertThat(offering)
                .isNotNull()
                .extracting(Offering::getId)
                .isNotNull();
    }

    @Test
    @ResetDB
    public void deleteCreatedOfferingTest() {
        Boolean existsAfterDelete =
                offeringRepository.save(
                    new Offering("New product", "Description", "product.img", BigDecimal.valueOf(50000)))
                        .flatMap(offering -> offeringRepository.delete(offering)
                                .thenReturn(offering.getId()))
                        .flatMap(id -> offeringRepository.existsById(id))
                        .block();
        assertNotNull(existsAfterDelete);
        assertFalse(existsAfterDelete);
    }

    @Test
    public void getOfferingFromCartTest() {
        OfferingWithCartItem offering = offeringRepository.findByIdWithCart(2L, 1L).block();

        assertNotNull(offering);
        assertEquals(2L, offering.offeringId());
        assertEquals("Беспроводная мышь", offering.offeringTitle());
        assertEquals(2, offering.amountInCart());
    }

    @Test
    public void getOfferingNotInCartTest() {
        OfferingWithCartItem offering = offeringRepository.findByIdWithCart(3L, 1L).block();

        assertNotNull(offering);
        assertEquals(3L, offering.offeringId());
        assertEquals("Рюкзак городской", offering.offeringTitle());
        assertNull(offering.amountInCart());
    }

    @Test
    public void getNonExistingOfferingTest() {
        assertNull(offeringRepository.findByIdWithCart(400L, 1L).block());
    }

    @Test
    @ResetDB
    public void createOfferingTest() {
        Offering offeringRequest = new Offering("Кроссовки",
                "Кроссовки New Balance",
                "newbalance.img",
                BigDecimal.valueOf(20000));

        Offering offering = offeringRepository.save(offeringRequest).block();

        assertNotNull(offering);
        assertNotNull(offering.getId());
        assertEquals("Кроссовки", offering.getTitle());
        assertEquals("Кроссовки New Balance", offering.getDescription());
        assertEquals("newbalance.img", offering.getImgPath());
        assertEquals(BigDecimal.valueOf(20000), offering.getPrice());
    }
}
