package com.tarasov.market.controller;

import com.tarasov.market.model.dto.ActionType;
import com.tarasov.market.model.dto.SortType;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {

    @GetMapping("/cart/items")
    public String loadCartPage() {
        return "cart";
    }

    @PostMapping("/cart/items")
    public String updateCartFromCartPage(@RequestParam @Positive long id,
                                         @RequestParam ActionType action,
                                         Model model) {
        return "cart";
    }

    @PostMapping("/items")
    public String updateCartFromMainPage(@RequestParam @Positive long id,
                                         @RequestParam(defaultValue = "") String search,
                                         @RequestParam(defaultValue = "NO") SortType sort,
                                         @RequestParam(defaultValue = "1") @Positive int pageNumber,
                                         @RequestParam(defaultValue = "5") @Positive int pageSize,
                                         @RequestParam ActionType action) {
        return String.format("redirect:/items?search=%s&sort=%s&pageNumber=%d&pageSize=%d",
                search, sort, pageNumber, pageSize);
    }

    @PostMapping("/items/{id}")
    public String updateCartFromOfferingPage(@PathVariable @Positive long id,
                                             @RequestParam ActionType action,
                                             Model model) {
        return "item";
    }
}
