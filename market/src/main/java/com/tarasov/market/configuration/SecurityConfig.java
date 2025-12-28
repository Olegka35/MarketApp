package com.tarasov.market.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.net.URI;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(ex -> ex
                        .pathMatchers("/items/new").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET, "/login", "/logout", "/register").permitAll()
                        .pathMatchers(HttpMethod.POST, "/register").permitAll()
                        .anyExchange().authenticated()
                )
                .formLogin(formLoginSpec ->  formLoginSpec.loginPage("/login"))
                .logout(this::setupLogout)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private void setupLogout(ServerHttpSecurity.LogoutSpec logoutSpec) {
        logoutSpec
                .requiresLogout(ServerWebExchangeMatchers.pathMatchers(HttpMethod.GET, "/logout"))
                .logoutSuccessHandler(((exchange, authentication) ->
                        exchange.getExchange().getSession()
                                .flatMap(WebSession::invalidate)
                                .then(Mono.fromRunnable(() -> {
                                    exchange.getExchange().getResponse().getHeaders().setLocation(URI.create("/items"));
                                    exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND);
                                }))
                ));
    }
}
