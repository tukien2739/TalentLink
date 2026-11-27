package com.example.talentlink.config;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.filter.OncePerRequestFilter;

class LockedAccountFilter extends OncePerRequestFilter {

    private final JdbcClient jdbc;
    private final SecurityContextLogoutHandler sessionLogout = new SecurityContextLogoutHandler();
    private final CookieClearingLogoutHandler rememberMeLogout = new CookieClearingLogoutHandler("remember-me");

    LockedAccountFilter(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/webjars/") || path.equals("/css/talentlink.css");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            boolean active = jdbc.sql("SELECT NOT is_locked FROM users WHERE email = :email")
                    .param("email", authentication.getName())
                    .query(Boolean.class)
                    .optional()
                    .orElse(false);
            if (!active) {
                sessionLogout.logout(request, response, authentication);
                rememberMeLogout.logout(request, response, authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
