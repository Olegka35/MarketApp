package com.tarasov.market.service;

import com.tarasov.market.model.dto.CartResponse;


public interface CartService {
    CartResponse getCartItems();
    void addOneCartItem(Long offeringId);
    void removeOneCartItem(Long offeringId);
    void deleteCartItem(Long offeringId);
}
