package com.tarasov.market.repository;


import com.tarasov.market.model.Offering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OfferingRepository extends CrudRepository<Offering, Long> {
}
