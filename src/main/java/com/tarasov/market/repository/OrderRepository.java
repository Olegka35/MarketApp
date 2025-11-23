package com.tarasov.market.repository;

import com.tarasov.market.model.db.OrderWithItem;
import com.tarasov.market.model.entity.Order;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;


public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

    @Query("""
    SELECT 
        o.id,
        o.total_price,
        oi.id order_item_id,
        oi.amount order_item_amount,
        oi.unit_price order_item_price,
        offerings.title offering_title
    FROM orders o
    JOIN order_items oi on oi.order_id = o.id
    JOIN offerings on offerings.id = oi.offering_id
    """)
    Flux<OrderWithItem> findAllWithItems();

    @Query("""
    SELECT 
        o.id,
        o.total_price,
        oi.id order_item_id,
        oi.amount order_item_amount,
        oi.unit_price order_item_price,
        offerings.title offering_title
    FROM orders o
    JOIN order_items oi on oi.order_id = o.id
    JOIN offerings on offerings.id = oi.offering_id
    WHERE o.id = :id
    """)
    Flux<OrderWithItem> findByIdWithItems(Long id);
}
