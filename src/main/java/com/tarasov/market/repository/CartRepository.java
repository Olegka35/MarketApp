package com.tarasov.market.repository;


import com.tarasov.market.model.CartItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends ListCrudRepository<CartItem, Long> {

    @EntityGraph(attributePaths = {"offering"})
    @Query("SELECT c FROM CartItem c")
    List<CartItem> findAllWithOffering();

    @EntityGraph(attributePaths = {"offering"})
    Optional<CartItem> findByOffering_Id(Long offeringId);
}
