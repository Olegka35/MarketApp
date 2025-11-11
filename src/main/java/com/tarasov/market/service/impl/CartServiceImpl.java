package com.tarasov.market.service.impl;


import com.tarasov.market.model.dto.CartItemDto;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    public CartResponse getCartItems() {
        List<CartItemDto> cartItems = cartRepository.findAll()
                .stream()
                .map(CartItemDto::from)
                .toList();
        BigDecimal totalPrice = cartItems
                .stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.count())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cartItems, totalPrice);
    }
}
