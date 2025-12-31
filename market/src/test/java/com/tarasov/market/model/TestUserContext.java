package com.tarasov.market.model;

import com.tarasov.market.model.entity.ExtendedUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import reactor.util.context.Context;

public class TestUserContext {

    public static Context user() {
        return authenticatedUser(1L, "Oleg", "pass", "USER");
    }

    public static Context admin() {
        return authenticatedUser(1L, "Oleg", "pass", "ADMIN");
    }

    public static Context authenticatedUser(Long ID, String username, String password, String role) {
        UserDetails userDetails
                = new ExtendedUserDetails(ID, username, password, role);
        Authentication authentication
                = new UsernamePasswordAuthenticationToken(userDetails,  password, userDetails.getAuthorities());
        return ReactiveSecurityContextHolder.withAuthentication(authentication);
    }
}
