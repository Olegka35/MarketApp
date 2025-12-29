package com.tarasov.market.service;


import com.tarasov.market.model.db.OfferingWithCartItem;
import com.tarasov.market.model.entity.CartItem;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.model.type.PaymentError;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.ConnectException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private PaymentService paymentService;

    @Test
    public void getCartItemsTest() {
        OfferingWithCartItem item1 = new OfferingWithCartItem(1L,
                "Test", "Test", "test.png", BigDecimal.valueOf(100),
                10L, 1);

        OfferingWithCartItem item2 = new OfferingWithCartItem(5L,
                "Test", "Test", "test.png", BigDecimal.valueOf(200),
                11L, 4);

        when(cartRepository.findAllWithOffering(1L)).thenReturn(Flux.just(item1, item2));
        when(paymentService.getAccountBalance()).thenReturn(Mono.just(BigDecimal.valueOf(10000)));

        CartResponse cart = cartService.getCartItems().block();

        assertNotNull(cart);
        assertEquals(2, cart.getCartItems().size());
        assertTrue(cart.getCartItems()
                .stream()
                .allMatch(ci -> ci.id() == 1L || ci.id() == 5L));
        assertEquals(BigDecimal.valueOf(900), cart.getTotalPrice());
        assertNull(cart.getError());
    }

    @Test
    public void getCartItemsTest_insufficientBalance() {
        OfferingWithCartItem item1 = new OfferingWithCartItem(1L,
                "Test", "Test description", "test.png", BigDecimal.valueOf(100),
                10L, 1);

        when(cartRepository.findAllWithOffering(1L)).thenReturn(Flux.just(item1));
        when(paymentService.getAccountBalance()).thenReturn(Mono.just(BigDecimal.valueOf(5)));

        CartResponse cart = cartService.getCartItems().block();

        assertNotNull(cart);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(1L, cart.getCartItems().getFirst().id());
        assertEquals(BigDecimal.valueOf(100), cart.getCartItems().getFirst().price());
        assertEquals("Test", cart.getCartItems().getFirst().title());
        assertEquals("Test description", cart.getCartItems().getFirst().description());
        assertEquals(BigDecimal.valueOf(100), cart.getTotalPrice());
        assertEquals(PaymentError.INSUFFICIENT_BALANCE, cart.getError());
    }

    @Test
    public void getCartItemsTest_paymentServiceNotAvailable() {
        OfferingWithCartItem item1 = new OfferingWithCartItem(1L,
                "Test", "Test description", "test.png", BigDecimal.valueOf(100),
                10L, 1);

        when(cartRepository.findAllWithOffering(1L)).thenReturn(Flux.just(item1));
        when(paymentService.getAccountBalance()).thenReturn(Mono.error(new ConnectException()));

        CartResponse cart = cartService.getCartItems().block();

        assertNotNull(cart);
        assertEquals(1, cart.getCartItems().size());
        assertEquals(1L, cart.getCartItems().getFirst().id());
        assertEquals(BigDecimal.valueOf(100), cart.getCartItems().getFirst().price());
        assertEquals("Test", cart.getCartItems().getFirst().title());
        assertEquals("Test description", cart.getCartItems().getFirst().description());
        assertEquals(BigDecimal.valueOf(100), cart.getTotalPrice());
        assertEquals(PaymentError.PAYMENT_SERVICE_NOT_AVAILABLE, cart.getError());
    }

    @Test
    public void deleteCartItemTest() {
        long id = 1L;
        when(cartRepository.existsByOfferingId(id)).thenReturn(Mono.just(true));
        when(cartRepository.deleteByOfferingId(id)).thenReturn(Mono.empty().then());

        cartService.deleteCartItem(id).block();

        verify(cartRepository).deleteByOfferingId(id);
    }

    @Test
    public void deleteCartItemTest_itemNotFoundInCart() {
        long id = 1L;
        when(cartRepository.existsByOfferingId(id)).thenReturn(Mono.just(false));
        when(cartRepository.deleteByOfferingId(id)).thenReturn(Mono.empty().then());

        assertThrows(NoSuchElementException.class, () -> cartService.deleteCartItem(id).block());
    }

    @Test
    public void addOneCartItemTest_existsInCart() {
        long id = 5L;
        CartItem cartItem = new CartItem(id, 1, 1L);
        when(cartRepository.findByOfferingId(id)).thenReturn(Mono.just(cartItem));
        when(offeringRepository.existsById(id)).thenReturn(Mono.just(true));
        when(cartRepository.save(any(CartItem.class)))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        cartService.addOneCartItem(id).block();

        ArgumentCaptor<CartItem> sentCartItem = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(sentCartItem.capture());
        assertEquals(2, sentCartItem.getValue().getAmount());
    }

    @Test
    public void addOneCartItemTest_firstInCart() {
        long id = 5L;
        when(cartRepository.findByOfferingId(id)).thenReturn(Mono.empty());
        when(offeringRepository.existsById(id)).thenReturn(Mono.just(true));
        when(cartRepository.save(any(CartItem.class)))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        cartService.addOneCartItem(id).block();

        verify(cartRepository).save(new CartItem(id, 1, 1L));
    }

    @Test
    public void addOneCartItemTest_incorrectOfferingId() {
        long id = 5L;
        when(cartRepository.findByOfferingId(id)).thenReturn(Mono.empty());
        when(offeringRepository.existsById(id)).thenReturn(Mono.just(false));

        assertThrows(NoSuchElementException.class, () -> cartService.addOneCartItem(id).block());
    }

    @Test
    public void removeOneCartItemTest_notLastInCart() {
        long id = 5L;
        CartItem cartItem = new CartItem(id, 3, 1L);
        when(cartRepository.findByOfferingId(id)).thenReturn(Mono.just(cartItem));
        when(cartRepository.save(any(CartItem.class)))
                .thenAnswer(i -> Mono.just(i.getArgument(0)));

        cartService.removeOneCartItem(id).block();

        ArgumentCaptor<CartItem> sentCartItem = ArgumentCaptor.forClass(CartItem.class);
        verify(cartRepository).save(sentCartItem.capture());
        assertEquals(2, sentCartItem.getValue().getAmount());
    }

    @Test
    public void removeOneCartItemTest_lastInCart() {
        long id = 5L;
        CartItem cartItem = new CartItem(id, 1, 1L);
        when(cartRepository.findByOfferingId(id)).thenReturn(Mono.just(cartItem));
        when(cartRepository.delete(cartItem)).thenReturn(Mono.empty().then());

        cartService.removeOneCartItem(id).block();

        verify(cartRepository).delete(cartItem);
    }

    @Test
    public void removeOneCartItemTest_notExistsInCart() {
        long id = 5L;
        when(cartRepository.findByOfferingId(id)).thenReturn(Mono.empty());

        assertThrows(NoSuchElementException.class, () -> cartService.removeOneCartItem(id).block());
    }
}
