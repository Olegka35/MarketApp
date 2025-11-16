package com.tarasov.market.service.impl;


import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.Offering;
import com.tarasov.market.model.dto.CartItemDto;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.repository.CartRepository;
import com.tarasov.market.repository.OfferingRepository;
import com.tarasov.market.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final OfferingRepository offeringRepository;

    @Override
    public CartResponse getCartItems() {
        List<CartItemDto> cartItems = cartRepository.findAllWithOffering()
                .stream()
                .map(CartItemDto::from)
                .toList();
        BigDecimal totalPrice = cartItems
                .stream()
                .map(item -> item.price().multiply(BigDecimal.valueOf(item.count())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cartItems, totalPrice);
    }

    @Override
    @Transactional
    public void addOneCartItem(Long offeringId) {
        cartRepository.findByOffering_Id(offeringId)
                .ifPresentOrElse(
                        item -> {
                            item.setAmount(item.getAmount() + 1);
                            cartRepository.save(item);
                        },
                        () -> {
                            Offering offering = offeringRepository.findById(offeringId)
                                    .orElseThrow(() -> new EntityNotFoundException("Offering not found"));
                            cartRepository.save(new CartItem(offering, 1));
                        }
                );
    }

    @Override
    @Transactional
    public void removeOneCartItem(Long offeringId) {
        CartItem item = cartRepository.findByOffering_Id(offeringId)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found"));
        if (item.getAmount() == 1) {
            item.getOffering().setCartItem(null);
            cartRepository.delete(item);
        } else {
            item.setAmount(item.getAmount() - 1);
            cartRepository.save(item);
        }
    }

    @Override
    @Transactional
    public void deleteCartItem(Long offeringId) {
        CartItem item = cartRepository.findByOffering_Id(offeringId)
                .orElseThrow(() -> new EntityNotFoundException("Cart Item not found"));
        item.getOffering().setCartItem(null);
        cartRepository.delete(item);
    }
}
