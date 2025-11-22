package com.tarasov.market.repository;

import com.tarasov.market.model.entity.Offering;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class OfferingRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private OfferingRepository offeringRepository;

    @Test
    public void findAllOfferingsTest() {
        assertThat(offeringRepository.findAllWithCart()).hasSize(5);
    }

    @Test
    public void findOfferingsWithPriceSortingTest() {
        List<Offering> offerings = offeringRepository.findAllWithCart(Sort.by(Sort.Direction.ASC, "price"));
        assertThat(offerings).hasSize(5);
        assertThat(offerings.getFirst().getPrice()).isEqualTo(new BigDecimal("990"));
        assertThat(offerings.getLast().getPrice()).isEqualTo(new BigDecimal("2790"));
    }

    @Test
    public void findOfferingsWithPriceSortingAndPaginationTest() {
        List<Offering> offerings = offeringRepository
                .findAllWithCart(PageRequest.of(1, 3, Sort.by(Sort.Direction.ASC, "price")))
                .getContent();
        assertThat(offerings).hasSize(2);
    }

    @Test
    public void findOfferingsWithNameSortingTest() {
        List<Offering> offerings = offeringRepository.findAllWithCart(Sort.by(Sort.Direction.ASC, "title"));
        assertThat(offerings).hasSize(5);
        assertThat(offerings.getFirst().getTitle()).isEqualTo("Беспроводная мышь");
        assertThat(offerings.getLast().getTitle()).isEqualTo("Термокружка 500 мл");
    }

    @Test
    public void searchOfferingsByTitleTest() {
        List<Offering> offerings = offeringRepository.findByTitleContainsOrDescriptionContains(" и ", " и ");
        assertThat(offerings).hasSize(2);
    }

    @Test
    @Transactional
    public void addOfferingTest() {
        Offering offering = offeringRepository.save(
                new Offering("New product", "Description", "product.img", BigDecimal.valueOf(50000))
        );

        assertThat(offering)
                .isNotNull()
                .extracting(Offering::getId)
                .isNotNull();
    }

    @Test
    @Transactional
    public void deleteCreatedOfferingTest() {
        Offering offering = offeringRepository.save(
                new Offering("New product", "Description", "product.img", BigDecimal.valueOf(50000))
        );
        offeringRepository.delete(offering);
        assertThat(offeringRepository.existsById(offering.getId())).isFalse();
    }

    @Test
    public void getOfferingFromCartTest() {
        Optional<Offering> offering = offeringRepository.findById(2L);
        assertTrue(offering.isPresent());
        assertEquals(2L, offering.get().getId());
        assertEquals("Беспроводная мышь", offering.get().getTitle());
        assertEquals(2, offering.get().getCartItem().getAmount());
    }

    @Test
    public void getOfferingNotInCartTest() {
        Optional<Offering> offering = offeringRepository.findById(3L);
        assertTrue(offering.isPresent());
        assertEquals(3L, offering.get().getId());
        assertEquals("Рюкзак городской", offering.get().getTitle());
        assertNull(offering.get().getCartItem());
    }

    @Test
    public void getNonExistingOfferingTest() {
        Optional<Offering> offering = offeringRepository.findById(400L);
        assertFalse(offering.isPresent());
    }

    @Test
    @Transactional
    public void createOfferingTest() {
        Offering offeringRequest = new Offering("Кроссовки",
                "Кроссовки New Balance",
                "newbalance.img",
                BigDecimal.valueOf(20000));
        Offering offering = offeringRepository.save(offeringRequest);

        assertEquals("Кроссовки", offering.getTitle());
        assertEquals("Кроссовки New Balance", offering.getDescription());
        assertEquals("newbalance.img", offering.getImgPath());
        assertEquals(BigDecimal.valueOf(20000), offering.getPrice());
    }
}
