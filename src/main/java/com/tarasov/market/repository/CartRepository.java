package com.tarasov.market.repository;


import com.tarasov.market.model.CartItem;
import com.tarasov.market.model.dto.db.OfferingWithCartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface CartRepository extends ReactiveCrudRepository<CartItem, Long> {

    @Query("""
    SELECT c.id cart_item_id,
           c.amount amount_in_cart,
           o.id offering_id,
           o.title offering_title,
           o.description offering_description,
           o.img_path offering_img_path,
           o.price offering_price
    FROM cart c
    JOIN offerings o ON o.id = c.offering_id
    """)
    Flux<OfferingWithCartItem> findAllWithOffering();

    @Query("SELECT EXISTS(SELECT 1 FROM cart WHERE offering_id = :offeringId)")
    Mono<Boolean> existsByOfferingId(Long offeringId);
}
