package com.tarasov.payment.repository;

import com.tarasov.payment.model.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account,Long> {
}
