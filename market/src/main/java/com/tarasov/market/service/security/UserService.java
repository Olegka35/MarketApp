package com.tarasov.market.service.security;

import com.tarasov.market.model.entity.User;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<User> registerAccount(String username, String password);
}
