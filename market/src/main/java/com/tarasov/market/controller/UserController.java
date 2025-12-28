package com.tarasov.market.controller;

import com.tarasov.market.model.exception.UserAlreadyExistsException;
import com.tarasov.market.service.security.UserService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
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
    public Mono<Rendering> registerAccount(@RequestPart @NotBlank String username,
                                           @RequestPart @NotBlank String password) {
        return userService.registerAccount(username, password)
                .doOnError(UserAlreadyExistsException.class,
                        e -> Rendering.view("register").build())
                .map(user -> Rendering.view("login").build());
    }
}
