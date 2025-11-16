package com.tarasov.market.service;


import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.Offering;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.impl.CartServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private OfferingRepository offeringRepository;

    @Mock
    private CartRepository cartRepository;

    @Test
    public void getCartItemsTest() {
        Offering offering1 = new Offering();
        offering1.setId(1L);
        offering1.setPrice(BigDecimal.valueOf(100));

        Offering offering2 = new Offering();
        offering2.setId(5L);
        offering2.setPrice(BigDecimal.valueOf(200));

        when(cartRepository.findAllWithOffering())
                .thenReturn(List.of(new CartItem(offering1, 1), new CartItem(offering2, 4)));

        CartResponse cart = cartService.getCartItems();

        assertEquals(2, cart.getCartItems().size());
        assertTrue(cart.getCartItems()
                .stream()
                .allMatch(ci -> ci.id() == 1L || ci.id() == 5L));
        assertEquals(BigDecimal.valueOf(900), cart.getTotalPrice());
    }

    @Test
    public void deleteCartItemTest() {
        long id = 1L;
        CartItem cartItem = new CartItem(new Offering(), 1);
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.of(cartItem));

        cartService.deleteCartItem(id);

        verify(cartRepository).delete(cartItem);
    }

    @Test
    public void deleteCartItemTest_itemNotFoundInCart() {
        long id = 1L;
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.deleteCartItem(id));
    }

    @Test
    public void addOneCartItemTest_existsInCart() {
        long id = 5L;
        CartItem cartItem = new CartItem(new Offering(), 1);
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.of(cartItem));

        cartService.addOneCartItem(id);

        ArgumentCaptor<CartItem> sentCartItem = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(sentCartItem.capture());
        assertEquals(2, sentCartItem.getValue().getAmount());
    }

    @Test
    public void addOneCartItemTest_firstInCart() {
        long id = 5L;
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.empty());
        Offering offering = new Offering();
        when(offeringRepository.findById(id)).thenReturn(Optional.of(offering));

        cartService.addOneCartItem(id);

        verify(cartRepository).save(new CartItem(offering, 1));
    }

    @Test
    public void addOneCartItemTest_incorrectOfferingId() {
        long id = 5L;
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.empty());
        when(offeringRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.addOneCartItem(id));
    }

    @Test
    public void removeOneCartItemTest_notLastInCart() {
        long id = 5L;
        CartItem cartItem = new CartItem(new Offering(), 3);
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.of(cartItem));

        cartService.removeOneCartItem(id);

        ArgumentCaptor<CartItem> sentCartItem = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(sentCartItem.capture());
        assertEquals(2, sentCartItem.getValue().getAmount());
    }

    @Test
    public void removeOneCartItemTest_lastInCart() {
        long id = 5L;
        CartItem cartItem = new CartItem(new Offering(), 1);
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.of(cartItem));

        cartService.removeOneCartItem(id);

        verify(cartRepository).delete(cartItem);
    }

    @Test
    public void removeOneCartItemTest_notExistsInCart() {
        long id = 5L;
        when(cartRepository.findByOffering_Id(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.removeOneCartItem(id));
    }
}
