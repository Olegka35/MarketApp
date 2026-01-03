package com.tarasov.market.service.impl;

import com.tarasov.market.model.entity.User;
import com.tarasov.market.model.exception.UserAlreadyExistsException;
import com.tarasov.market.repository.UserRepository;
import com.tarasov.market.service.security.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<User> registerAccount(String username, String password) {
        return userRepository.findByUsername(username)
                .flatMap(existingUser ->
                        Mono.<User>error(new UserAlreadyExistsException("User " +  username + " already exists")))
                .switchIfEmpty(
                        userRepository.save(new User(username, passwordEncoder.encode(password)))
                );
    }
}
