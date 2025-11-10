package com.tarasov.market.controller;

import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OrderController {

    @GetMapping("/orders")
    public String getAllOrdersPage(Model model) {
        return "orders";
    }

    @GetMapping("/orders/{id}")
    public String getOrderPage(@PathVariable @Positive long id,
                               @RequestParam(defaultValue = "false") boolean newOrder,
                               Model model) {
        return "order";
    }

    @PostMapping("/buy")
    public String createNewOrder() {
        return "redirect:/orders/{id}?newOrder=true";
    }
}
