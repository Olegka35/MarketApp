package com.tarasov.market.controller;

import com.tarasov.market.model.dto.RegistrationRequest;
import com.tarasov.market.model.exception.UserAlreadyExistsException;
import com.tarasov.market.service.security.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public Mono<Rendering> openLoginPage() {
        return Mono.just(Rendering.view("login").build());
    }

    @GetMapping("/register")
    public Mono<Rendering> openRegisterPage() {
        return Mono.just(Rendering.view("register").build());
    }

    @PostMapping("/register")
    public Mono<Rendering> registerAccount(@Valid @ModelAttribute RegistrationRequest registrationRequest) {
        return userService.registerAccount(registrationRequest.username(), registrationRequest.password())
                .doOnError(UserAlreadyExistsException.class,
                        e -> Rendering.view("register").build())
                .map(user -> Rendering.view("login").build());
    }
}
