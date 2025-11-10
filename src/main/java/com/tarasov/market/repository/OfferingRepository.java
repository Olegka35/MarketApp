package com.tarasov.market.repository;


import com.tarasov.market.model.Offering;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OfferingRepository extends JpaRepository<Offering, Long> {

    List<Offering> findByTitleContainsOrDescriptionContains(String title, String description);

    List<Offering> findByTitleContainsOrDescriptionContains(String title, String description, Pageable pageable);
}
