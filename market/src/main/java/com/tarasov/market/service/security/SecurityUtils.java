package com.tarasov.market.service.security;

import com.tarasov.market.model.entity.ExtendedUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public class SecurityUtils {

    public static Mono<UserDetails> getUserDetails() {
        return ReactiveSecurityContextHolder.getContext()
                .mapNotNull(SecurityContext::getAuthentication)
                .mapNotNull(Authentication::getPrincipal)
                .cast(UserDetails.class);
    }

    public static Mono<Long> getUserId() {
        return getUserDetails()
                .cast(ExtendedUserDetails.class)
                .map(ExtendedUserDetails::getId);
    }

    public static Mono<String> getUserName() {
        return getUserDetails()
                .cast(ExtendedUserDetails.class)
                .map(ExtendedUserDetails::getUsername);
    }

    public static Flux<String> getUserRoles() {
        return getUserDetails()
                .cast(ExtendedUserDetails.class)
                .map(ExtendedUserDetails::getAuthorities)
                .map(list ->
                        list.stream().map(GrantedAuthority::getAuthority).toList())
                .flatMapMany(Flux::fromIterable);
    }
}
