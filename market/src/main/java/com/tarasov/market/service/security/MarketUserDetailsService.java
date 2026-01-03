package com.tarasov.market.service.security;

import com.tarasov.market.model.entity.ExtendedUserDetails;
import com.tarasov.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MarketUserDetailsService implements ReactiveUserDetailsService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new ExtendedUserDetails(user.getId(),
                        user.getUsername(),
                        user.getPassword(),
                        user.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER")
                )
                .cast(UserDetails.class)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)));
    }
}
