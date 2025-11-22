package com.tarasov.market.service.impl;


import com.tarasov.market.model.entity.CartItem;
import com.tarasov.market.model.dto.CartItemDto;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final OfferingRepository offeringRepository;

    @Override
    public Mono<CartResponse> getCartItems() {
        return cartRepository.findAllWithOffering()
                .map(CartItemDto::from)
                .collectList()
                .map(cartItems -> {
                    BigDecimal totalPrice = cartItems
                            .stream()
                            .map(item -> item.price().multiply(BigDecimal.valueOf(item.count())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new CartResponse(cartItems, totalPrice);
                });
    }

    @Override
    @Transactional
    public Mono<Void> addOneCartItem(Long offeringId) {
        return cartRepository.findByOfferingId(offeringId)
                .switchIfEmpty(
                        offeringRepository.findById(offeringId)
                                .switchIfEmpty(Mono.error(new NoSuchElementException("Offering not found")))
                                .flatMap(offering -> {
                                    CartItem newCartItem = new CartItem(offeringId, 1);
                                    return cartRepository.save(newCartItem);
                                })
                )
                .flatMap(cartItem -> {
                    cartItem.setAmount(cartItem.getAmount() + 1);
                    return cartRepository.save(cartItem).then();
                });
    }

    @Override
    @Transactional
    public Mono<Void> removeOneCartItem(Long offeringId) {
        return cartRepository.findByOfferingId(offeringId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Cart Item not found")))
                .flatMap(cartItem -> {
                    if (cartItem.getAmount() == 1) {
                        return cartRepository.delete(cartItem).then();
                    } else {
                        cartItem.setAmount(cartItem.getAmount() - 1);
                        return cartRepository.save(cartItem).then();
                    }
                });
    }

    @Override
    @Transactional
    public Mono<Void> deleteCartItem(Long offeringId) {
        return cartRepository.existsByOfferingId(offeringId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Cart Item not found")))
                .then(cartRepository.deleteByOfferingId(offeringId));
    }
}
