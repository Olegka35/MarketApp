package com.tarasov.market.service;

import com.tarasov.market.model.dto.OrderDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface OrderService {
    Flux<OrderDto> getOrders();
    Mono<OrderDto> getOrderById(Long id);
    Mono<OrderDto> createOrderFromCart();
}
