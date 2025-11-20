package com.tarasov.market.controller;

import com.tarasov.market.model.dto.type.ActionType;
import com.tarasov.market.model.dto.CartResponse;
import com.tarasov.market.model.dto.type.SortType;
import com.tarasov.market.service.CartService;
import com.tarasov.market.service.OfferingService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final OfferingService offeringService;

    @GetMapping("/cart/items")
    public String loadCartPage(Model model) {
        CartResponse cartResponse = cartService.getCartItems();
        model.addAttribute("items", cartResponse.getCartItems());
        model.addAttribute("total", cartResponse.getTotalPrice());
        return "cart";
    }

    @PostMapping("/cart/items")
    public String updateCartFromCartPage(@RequestParam @Positive long id,
                                         @RequestParam ActionType action) {
        updateCart(id, action);
        return "redirect:/cart/items";
    }

    @PostMapping("/items")
    public String updateCartFromMainPage(@RequestParam @Positive long id,
                                         @RequestParam(defaultValue = "") String search,
                                         @RequestParam(defaultValue = "NO") SortType sort,
                                         @RequestParam(defaultValue = "1") @Positive int pageNumber,
                                         @RequestParam(defaultValue = "5") @Positive int pageSize,
                                         @RequestParam ActionType action) {
        updateCart(id, action);
        return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                search, sort, pageNumber, pageSize);
    }

    @PostMapping("/items/{id}")
    public String updateCartFromOfferingPage(@PathVariable @Positive long id,
                                             @RequestParam ActionType action) {
        updateCart(id, action);
        return String.format("redirect:/items?%d", id);
    }

    private void updateCart(long offeringId, ActionType action) {
        if (action.equals(ActionType.MINUS)) {
            cartService.removeOneCartItem(offeringId);
        } else if (action.equals(ActionType.PLUS)) {
            cartService.addOneCartItem(offeringId);
        } else if (action.equals(ActionType.DELETE)) {
            cartService.deleteCartItem(offeringId);
        }
    }
}
