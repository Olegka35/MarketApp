package com.tarasov.market.service.security;

import com.tarasov.market.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class MarketUserPasswordService implements ReactiveUserDetailsPasswordService {

    private final UserRepository userRepository;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails user, String newPassword) {
        return userRepository.findByUsername(user.getUsername())
                .flatMap(userEntity -> {
                    userEntity.setPassword(newPassword);
                    return userRepository.save(userEntity);
                })
                .thenReturn(user);
    }
}
