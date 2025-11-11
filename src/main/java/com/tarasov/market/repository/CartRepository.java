package com.tarasov.market.repository;


import com.tarasov.market.model.CartItem;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends ListCrudRepository<CartItem, Long> {
}
