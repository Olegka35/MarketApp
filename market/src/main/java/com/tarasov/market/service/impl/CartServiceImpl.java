package com.tarasov.market.service.impl;


import com.tarasov.market.model.dto.CartItemDto;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.model.entity.CartItem;
import com.tarasov.market.model.type.PaymentError;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.CartService;
import com.tarasov.market.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final OfferingRepository offeringRepository;
    private final PaymentService paymentService;

    @Override
    public Mono<CartResponse> getCartItems() {
        return Mono.zip(
                cartRepository.findAllWithOffering(1L)
                        .map(CartItemDto::from)
                        .collectList()
                        .map(cartItems ->
                                new CartResponse(cartItems, calculateTotalPrice(cartItems))),
                paymentService.getAccountBalance()
                        .map(Optional::of)
                        .onErrorResume(error -> {
                            log.error("Error during GetBalance request to Payment Service", error);
                            return Mono.just(Optional.empty());
                        })
        ).map(this::combineCartResponse);
    }

    @Override
    @Transactional
    public Mono<Void> addOneCartItem(Long offeringId) {
        return cartRepository.findByOfferingIdAndUserId(offeringId, 1L)
                .flatMap(cartItem -> {
                    cartItem.setAmount(cartItem.getAmount() + 1);
                    return cartRepository.save(cartItem);
                })
                .switchIfEmpty(createCartItem(offeringId))
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> removeOneCartItem(Long offeringId) {
        return cartRepository.findByOfferingIdAndUserId(offeringId, 1L)
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
        return cartRepository.existsByOfferingIdAndUserId(offeringId, 1L)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Cart Item not found")))
                .then(cartRepository.deleteByOfferingIdAndUserId(offeringId, 1L));
    }

    private BigDecimal calculateTotalPrice(List<CartItemDto> cartItems) {
        return cartItems
                .stream()
                .map(item ->
                        item.price().multiply(BigDecimal.valueOf(item.count())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private CartResponse combineCartResponse(Tuple2<CartResponse, Optional<BigDecimal>> tupleResult) {
        CartResponse cartResponse = tupleResult.getT1();
        tupleResult.getT2().ifPresentOrElse(
                balance -> {
                    if (balance.compareTo(cartResponse.getTotalPrice()) < 0) {
                        cartResponse.setError(PaymentError.INSUFFICIENT_BALANCE);
                    }
                },
                () -> cartResponse.setError(PaymentError.PAYMENT_SERVICE_NOT_AVAILABLE)
        );
        return cartResponse;
    }

    private Mono<CartItem> createCartItem(Long offeringId) {
        return offeringRepository.existsById(offeringId)
                .flatMap(exists -> {
                    if (exists) {
                        CartItem newCartItem = new CartItem(offeringId, 1, 1L);
                        return cartRepository.save(newCartItem);
                    } else {
                        return Mono.error(new NoSuchElementException("Offering not found"));
                    }
                });
    }
}
