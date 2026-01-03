package com.tarasov.market.model;

import com.tarasov.market.model.entity.ExtendedUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import reactor.util.context.Context;

public class TestUserContext {

    public static Context user() {
        return createUserContext(1L, "Oleg", "pass", "USER");
    }

    public static Context admin() {
        return createUserContext(1L, "Oleg", "pass", "ADMIN");
    }

    public static Context createUserContext(Long ID, String username, String password, String role) {
        Authentication authentication = getAuthentication(ID, username, password, role);
        return ReactiveSecurityContextHolder.withAuthentication(authentication);
    }

    public static WebTestClientConfigurer mockUser() {
        return configureTestUser(1L, "Oleg", "pass", "USER");
    }

    public static WebTestClientConfigurer mockAdmin() {
        return configureTestUser(1L, "Oleg", "pass", "ADMIN");
    }

    public static WebTestClientConfigurer configureTestUser(Long ID, String username, String password, String role) {
        Authentication authentication = getAuthentication(ID, username, password, role);
        return SecurityMockServerConfigurers.mockAuthentication(authentication);
    }

    private static Authentication getAuthentication(Long ID, String username, String password, String role) {
        UserDetails userDetails
                = new ExtendedUserDetails(ID, username, password, "ROLE_" + role);
        return new UsernamePasswordAuthenticationToken(userDetails,  password, userDetails.getAuthorities());
    }
}
