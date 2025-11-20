package com.tarasov.market.repository;

import com.tarasov.market.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderRepository extends ListCrudRepository<Order, Long> {

    @EntityGraph(attributePaths = {
            "orderItems",
            "orderItems.offering",
            "orderItems.offering.cartItem"
    })
    @Query("SELECT o FROM Order o")
    List<Order> findAllWithItems();
}
