package com.tarasov.market.repository;


import com.tarasov.market.model.entity.Offering;
import com.tarasov.market.model.db.OfferingWithCartItem;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface OfferingRepository extends OfferingRepositoryCustom, ReactiveCrudRepository<Offering, Long> {

    @Query("""
    SELECT o.id offering_id,
           o.title offering_title,
           o.description offering_description,
           o.img_path offering_img_path,
           o.price offering_price,
           c.id cart_item_id,
           c.amount amount_in_cart
    FROM offerings o
    LEFT JOIN cart c ON o.id = c.offering_id
    """)
    Flux<OfferingWithCartItem> findAllWithCart();

    @Query("""
    SELECT o.id offering_id,
           o.title offering_title,
           o.description offering_description,
           o.img_path offering_img_path,
           o.price offering_price,
           0 cart_item_id,
           0 amount_in_cart
    FROM offerings o
    WHERE o.id = :offeringId
    """)
    Mono<OfferingWithCartItem> findByIdWithEmptyCart(Long offeringId);

    @Query("""
    SELECT o.id offering_id,
           o.title offering_title,
           o.description offering_description,
           o.img_path offering_img_path,
           o.price offering_price,
           c.id cart_item_id,
           c.amount amount_in_cart
    FROM offerings o
    LEFT JOIN cart c ON o.id = c.offering_id AND c.user_id = :userId
    WHERE o.id = :offeringId
    """)
    Mono<OfferingWithCartItem> findByIdWithCart(Long offeringId, Long userId);

    Mono<Integer> countByTitleContainingOrDescriptionContaining(String title, String description);
}
