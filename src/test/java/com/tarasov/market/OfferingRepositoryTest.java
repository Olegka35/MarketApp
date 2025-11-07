package com.tarasov.market;

import com.tarasov.market.model.Offering;
import com.tarasov.market.repository.OfferingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class OfferingRepositoryTest extends MarketAppApplicationTest {

    @Autowired
    private OfferingRepository offeringRepository;

    @Test
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
    public void deleteCreatedOfferingTest() {
        Offering offering = offeringRepository.save(
                new Offering("New product", "Description", "product.img", BigDecimal.valueOf(50000))
        );
        offeringRepository.delete(offering);
        assertThat(offeringRepository.existsById(offering.getId())).isFalse();
    }
}
