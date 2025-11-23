package com.tarasov.market.repository;

import com.tarasov.market.model.entity.OrderItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface OrderItemRepository extends ReactiveCrudRepository<OrderItem, Long> {
}
