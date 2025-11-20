package com.tarasov.market.repository;


import com.tarasov.market.model.Offering;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {

    @EntityGraph(attributePaths = {"cartItem"})
    @Query("select o from Offering o")
    List<Offering> findAllWithCart();

    @EntityGraph(attributePaths = {"cartItem"})
    @Query("select o from Offering o")
    List<Offering> findAllWithCart(Sort sort);

    @EntityGraph(attributePaths = {"cartItem"})
    @Query("select o from Offering o")
    Page<Offering> findAllWithCart(Pageable pageable);

    @EntityGraph(attributePaths = {"cartItem"})
    List<Offering> findByTitleContainsOrDescriptionContains(String title, String description);

    @EntityGraph(attributePaths = {"cartItem"})
    Page<Offering> findByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);
}
