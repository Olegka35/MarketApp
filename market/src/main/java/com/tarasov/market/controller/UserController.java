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
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public Mono<Rendering> openLoginPage(ServerWebExchange exchange) {
        Map<String, String> model = new HashMap<>();
        if (exchange.getRequest().getQueryParams().containsKey("error")) {
            model.put("error", "Incorrect username or password");
        }
        return Mono.just(Rendering.view("login")
                .model(model)
                .build());
    }

    @GetMapping("/register")
    public Mono<Rendering> openRegisterPage() {
        return Mono.just(Rendering.view("register").build());
    }

    @PostMapping("/register")
    public Mono<Rendering> registerAccount(@Valid @ModelAttribute RegistrationRequest registrationRequest) {
        return userService.registerAccount(registrationRequest.username(), registrationRequest.password())
                .map(user -> Rendering.view("login").build())
                .onErrorResume(UserAlreadyExistsException.class,
                        e -> Mono.just(
                                Rendering.view("register")
                                        .modelAttribute("error", e.getMessage())
                                        .build())
                );
    }
}
