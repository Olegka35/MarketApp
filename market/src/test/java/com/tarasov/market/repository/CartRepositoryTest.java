package com.tarasov.market.repository;

import com.tarasov.market.configuration.ResetDB;
import com.tarasov.market.model.entity.CartItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


public class CartRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Test
    public void findAllCartItemsTest() {
        assertThat(cartRepository.findAllWithOffering(1L).collectList().block())
                .hasSize(2);
    }

    @Test
    public void findCartItemByOfferingIdTest() {
        CartItem item = cartRepository.findByOfferingIdAndUserId(2L, 1L).block();
        assertNotNull(item);
        assertEquals(2L, item.getOfferingId());
    }

    @Test
    public void existsCartItemByOfferingIdTest() {
        Boolean exists = cartRepository.existsByOfferingIdAndUserId(2L, 1L).block();
        assertNotNull(exists);
        assertTrue(exists);
    }

    @Test
    public void notExistsCartItemByOfferingIdTest() {
        Boolean exists = cartRepository.existsByOfferingIdAndUserId(20L, 1L).block();
        assertNotNull(exists);
        assertFalse(exists);
    }

    @Test
    public void findNonExistingCartItemByOfferingIdTest() {
        Mono<CartItem> item = cartRepository.findByOfferingIdAndUserId(1L, 1L);
        assertNull(item.block());
    }

    @Test
    @ResetDB
    public void addCartItemTest() {
        Long offeringId = 1L;
        CartItem cartItem = new CartItem(offeringId, 1, 1L);
        CartItem createdCartItem = cartRepository.save(cartItem).block();
        assertNotNull(createdCartItem);
        assertEquals(offeringId, createdCartItem.getOfferingId());

        CartItem item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNotNull(item);
        assertEquals(offeringId, item.getOfferingId());
    }

    @Test
    @ResetDB
    public void deleteCartItemTest() {
        Long offeringId = 5L;
        CartItem item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNotNull(item);

        cartRepository.delete(item).block();

        item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNull(item);
    }

    @Test
    @ResetDB
    public void updateCartItemAmountTest() {
        Long offeringId = 5L;
        CartItem item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNotNull(item);
        assertEquals(1, item.getAmount());

        item.setAmount(2);
        cartRepository.save(item).block();

        item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNotNull(item);
        assertEquals(2, item.getAmount());
    }

    @Test
    @ResetDB
    public void deleteByOfferingIdTest() {
        Long offeringId = 5L;
        CartItem item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNotNull(item);

        cartRepository.deleteByOfferingIdAndUserId(offeringId, 1L).block();

        item = cartRepository.findByOfferingIdAndUserId(offeringId, 1L).block();
        assertNull(item);
    }
}
