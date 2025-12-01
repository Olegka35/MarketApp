package com.tarasov.market.service;

import com.tarasov.market.model.dto.CartResponse;
import reactor.core.publisher.Mono;


public interface CartService {
    Mono<CartResponse> getCartItems();
    Mono<Void> addOneCartItem(Long offeringId);
    Mono<Void> removeOneCartItem(Long offeringId);
    Mono<Void> deleteCartItem(Long offeringId);
}
