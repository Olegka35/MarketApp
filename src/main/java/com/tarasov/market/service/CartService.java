package com.tarasov.market.service;

import com.tarasov.market.model.dto.CartResponse;


public interface CartService {
    CartResponse getCartItems();
    void addCartItem(Long offeringId);
    void removeCartItem(Long offeringId);
}
