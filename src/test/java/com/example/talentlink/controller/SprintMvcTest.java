package com.example.talentlink.controller;

import com.example.talentlink.config.SecurityConfig;
import com.example.talentlink.service.StudentRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentRegistrationController.class)
@Import(SecurityConfig.class)
class SprintMvcTest {
    @Autowired MockMvc mvc;
    @Autowired PasswordEncoder encoder;
    @MockitoBean UserDetailsService users;
    @MockitoBean StudentRegistrationService registration;
    @MockitoBean(answers = org.mockito.Answers.RETURNS_DEEP_STUBS) JdbcClient jdbc;

    @Test
    void studentLoginRedirectsToProfile() throws Exception {
        stubStudent();
        mvc.perform(post("/login").with(csrfToken()).param("email", "student@example.com")
                        .param("password", "correct-password"))
                .andExpect(redirectedUrl("/me"))
                .andExpect(request().sessionAttribute(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                        org.hamcrest.Matchers.hasProperty("authentication",
                                org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is("student@example.com")))));
    }

    @Test
    void wrongPasswordReturnsToLogin() throws Exception {
        stubStudent();
        mvc.perform(post("/login").with(csrfToken()).param("email", "student@example.com")
                        .param("password", "wrong-password"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(request().sessionAttributeDoesNotExist(
                        HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/me", "/partner", "/admin"})
    void anonymousUserMustLogIn(String path) throws Exception {
        mvc.perform(get(path).accept(MediaType.TEXT_HTML)).andExpect(redirectedUrl("http://localhost/login"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/partner", "/admin"})
    void studentCannotOpenOtherRolePages(String path) throws Exception {
        when(jdbc.sql("SELECT NOT is_locked FROM users WHERE email = :email")
                .param("email", "student@example.com").query(Boolean.class).optional())
                .thenReturn(Optional.of(true));

        mvc.perform(get(path).session(studentSession()))
                .andExpect(status().isForbidden());
    }

    @Test
    void invalidRegistrationShowsFieldErrors() throws Exception {
        mvc.perform(post("/register").with(csrfToken()).param("fullName", "").param("email", "")
                        .param("password", "short").param("passwordConfirmation", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeHasFieldErrors("registration", "fullName", "email", "password"));
        verifyNoInteractions(registration);
    }

    @Test
    void registrationWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/register")).andExpect(status().isForbidden());
        verifyNoInteractions(registration);
    }

    private RequestPostProcessor csrfToken() {
        return request -> {
            var repository = new HttpSessionCsrfTokenRepository();
            var token = repository.generateToken(request);
            repository.saveToken(token, request, null);
            new XorCsrfTokenRequestAttributeHandler().handle(request, new MockHttpServletResponse(), () -> token);
            var maskedToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            request.addParameter(maskedToken.getParameterName(), maskedToken.getToken());
            return request;
        };
    }

    private MockHttpSession studentSession() {
        var principal = User.withUsername("student@example.com").password("unused").roles("STUDENT").build();
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities()));
        var session = new MockHttpSession();
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return session;
    }

    private void stubStudent() {
        when(users.loadUserByUsername("student@example.com")).thenReturn(User.withUsername("student@example.com")
                .password(encoder.encode("correct-password")).roles("STUDENT").build());
    }
}
