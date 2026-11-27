package com.example.talentlink.config;

import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

class RoleHomeAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Map<String, String> HOME_BY_AUTHORITY = Map.of(
            "ROLE_ADMIN", "/admin",
            "ROLE_PARTNER", "/partner",
            "ROLE_STUDENT", "/me");

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(HOME_BY_AUTHORITY::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("/");
    }
}
