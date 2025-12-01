package com.tarasov.market.controller;

import com.tarasov.market.service.OrderService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;


@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public Mono<Rendering> getAllOrdersPage() {
        return orderService.getOrders()
                .collectList()
                .map(orders ->
                        Rendering.view("orders")
                                .modelAttribute("orders", orders)
                                .build());
    }

    @GetMapping("/orders/{id}")
    public Mono<Rendering> getOrderPage(@PathVariable @Positive long id,
                                        @RequestParam(defaultValue = "false") boolean newOrder) {
        return orderService.getOrderById(id)
                .map(order ->
                        Rendering.view("order")
                                .modelAttribute("order", order)
                                .modelAttribute("newOrder", newOrder)
                                .build());
    }

    @PostMapping("/buy")
    public Mono<Rendering> createNewOrder() {
        return orderService.createOrderFromCart()
                .map(newOrder ->
                        Rendering.redirectTo(String.format("/orders/%d?newOrder=true", newOrder.id()))
                                .build());
    }
}
