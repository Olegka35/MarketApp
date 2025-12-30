package com.tarasov.market.service.impl;


import com.tarasov.market.model.dto.CartItemDto;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.model.entity.CartItem;
import com.tarasov.market.model.type.PaymentError;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.CartService;
import com.tarasov.market.service.PaymentService;
import com.tarasov.market.service.security.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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
        return SecurityUtils.getUserId()
                .flatMap(this::getUserCartItems);
    }

    private Mono<CartResponse> getUserCartItems(Long userId) {
        return Mono.zip(
                cartRepository.findAllWithOffering(userId)
                        .map(CartItemDto::from)
                        .collectList()
                        .map(cartItems ->
                                new CartResponse(cartItems, calculateTotalPrice(cartItems))),
                paymentService.getAccountBalance(userId)
                        .map(Optional::of)
                        .onErrorResume(this::processGetBalanceError)
        ).map(this::combineCartResponse);
    }

    @Override
    @Transactional
    public Mono<Void> addOneCartItem(Long offeringId) {
        return SecurityUtils.getUserId()
                .flatMap(userId -> addOneCartItem(offeringId, userId));
    }

    private Mono<Void> addOneCartItem(Long offeringId, Long userId) {
        return cartRepository.findByOfferingIdAndUserId(offeringId, userId)
                .flatMap(cartItem -> {
                    cartItem.setAmount(cartItem.getAmount() + 1);
                    return cartRepository.save(cartItem);
                })
                .switchIfEmpty(createCartItem(offeringId, userId))
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> removeOneCartItem(Long offeringId) {
        return SecurityUtils.getUserId()
                .flatMap(userId -> removeOneCartItem(offeringId, userId));
    }

    private Mono<Void> removeOneCartItem(Long offeringId, Long userId) {
        return cartRepository.findByOfferingIdAndUserId(offeringId, userId)
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
        return SecurityUtils.getUserId()
                .flatMap(userId -> deleteCartItem(offeringId, userId));
    }

    private Mono<Void> deleteCartItem(Long offeringId, Long userId) {
        return cartRepository.existsByOfferingIdAndUserId(offeringId, userId)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Cart Item not found")))
                .then(cartRepository.deleteByOfferingIdAndUserId(offeringId, userId));
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

    private Mono<CartItem> createCartItem(Long offeringId, Long userId) {
        return offeringRepository.existsById(offeringId)
                .flatMap(exists -> {
                    if (exists) {
                        CartItem newCartItem = new CartItem(offeringId, 1, userId);
                        return cartRepository.save(newCartItem);
                    } else {
                        return Mono.error(new NoSuchElementException("Offering not found"));
                    }
                });
    }

    private Mono<Optional<BigDecimal>> processGetBalanceError(Throwable error) {
        log.error("Error during GetBalance request to Payment Service", error);
        return error instanceof WebClientResponseException.NotFound
                ? Mono.just(Optional.of(BigDecimal.ZERO))
                : Mono.just(Optional.empty());
    }
}
