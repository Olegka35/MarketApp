package com.tarasov.market.repository;


import com.tarasov.market.model.CartItem;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends ListCrudRepository<CartItem, Long> {
    Optional<CartItem> findByOffering_Id(Long offeringId);
}
