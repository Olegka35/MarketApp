package com.tarasov.market;

import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.Offering;
import com.tarasov.market.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartRepositoryTest extends MarketAppApplicationTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void findAllCartItemsTest() {
        assertThat(cartRepository.findAll())
                .hasSize(2);
    }

    @Test
    public void findCartItemByOfferingIdTest() {
        Optional<CartItem> item = cartRepository.findByOffering_Id(2L);
        assertTrue(item.isPresent());
        assertEquals(2L, item.get().getOffering().getId());
    }

    @Test
    public void findNonExistingCartItemByOfferingIdTest() {
        Optional<CartItem> item = cartRepository.findByOffering_Id(1L);
        assertTrue(item.isEmpty());
    }

    @Test
    @Transactional
    public void addCartItemTest() {
        Long offeringId = 1L;
        Offering offering = new Offering();
        offering.setId(offeringId);
        CartItem cartItem = new CartItem(offering, 1);
        cartRepository.save(cartItem);

        Optional<CartItem> item = cartRepository.findByOffering_Id(offeringId);
        assertTrue(item.isPresent());
    }

    @Test
    @Transactional
    public void deleteCartItemTest() {
        Long offeringId = 5L;
        Optional<CartItem> item = cartRepository.findByOffering_Id(offeringId);
        assertTrue(item.isPresent());

        item.get().getOffering().setCartItem(null);
        cartRepository.delete(item.get());

        item = cartRepository.findByOffering_Id(offeringId);
        assertTrue(item.isEmpty());
    }

    @Test
    @Transactional
    public void updateCartItemAmountTest() {
        Long offeringId = 5L;
        Optional<CartItem> item = cartRepository.findByOffering_Id(offeringId);
        assertTrue(item.isPresent());
        assertEquals(1, item.get().getAmount());

        item.get().setAmount(2);
        cartRepository.save(item.get());

        item = cartRepository.findByOffering_Id(offeringId);
        assertTrue(item.isPresent());
        assertEquals(2, item.get().getAmount());
    }
}
