package com.tarasov.market.repository;


import com.tarasov.market.model.Offering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface OfferingRepository extends CrudRepository<Offering, BigInteger> {
}
