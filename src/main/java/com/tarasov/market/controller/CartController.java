package com.tarasov.market.controller;

import com.tarasov.market.model.dto.CartUpdateRequest;
import com.tarasov.market.model.type.ActionType;
import com.tarasov.market.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart/items")
    public Mono<Rendering> loadCartPage() {
        return cartService.getCartItems()
                .map(cartResponse ->
                        Rendering.view("cart")
                                .modelAttribute("items", cartResponse.getCartItems())
                                .modelAttribute("total", cartResponse.getTotalPrice())
                                .build());
    }

    @PostMapping("/cart/items")
    public Mono<Rendering> updateCartFromCartPage(@ModelAttribute CartUpdateRequest request) {
        return updateCart(request.id(), request.action())
                .then(
                        Mono.just(Rendering.redirectTo("/cart/items").build())
                );
    }

    @PostMapping("/items")
    public Mono<Rendering> updateCartFromMainPage(@Valid @ModelAttribute CartUpdateRequest request) {
        return updateCart(request.id(), request.action())
                .then(Mono.just(
                        Rendering.redirectTo(String.format("/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                                        request.search(), request.sort(), request.pageNumber(), request.pageSize()))
                                .build())
                );
    }

    @PostMapping("/items/{id}")
    public Mono<Rendering> updateCartFromOfferingPage(@PathVariable @Positive long id,
                                                      @ModelAttribute CartUpdateRequest request) {
        return updateCart(id, request.action())
                .then(
                        Mono.just(Rendering.redirectTo(String.format("/items/%d", id)).build())
                );
    }

    private Mono<Void> updateCart(long offeringId, ActionType action) {
        return switch (action) {
            case ActionType.MINUS -> cartService.removeOneCartItem(offeringId);
            case ActionType.PLUS -> cartService.addOneCartItem(offeringId);
            case ActionType.DELETE -> cartService.deleteCartItem(offeringId);
        };
    }
}
