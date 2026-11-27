package com.example.talentlink.config;

import java.util.Locale;

import jakarta.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.StringUtils;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsService users,
            @Value("${TALENTLINK_REMEMBER_ME_KEY:}") String rememberMeKey) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers("/forgot-password", "/reset-password").anonymous()
                        .requestMatchers("/css/talentlink.css", "/webjars/bootstrap/**").permitAll()
                        .requestMatchers("/me", "/me/**", "/candidate", "/candidate/**").hasRole("STUDENT")
                        .requestMatchers("/partner", "/partner/**", "/recruiter", "/recruiter/**").hasRole("PARTNER")
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .requestCache(cache -> cache.disable())
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(new RoleHomeAuthenticationSuccessHandler())
                        .permitAll())
                .rememberMe(rememberMe -> {
                    rememberMe.userDetailsService(users);
                    if (StringUtils.hasText(rememberMeKey)) {
                        rememberMe.key(rememberMeKey);
                    }
                })
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/?logout"))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(JdbcClient jdbc) {
        return email -> jdbc.sql("""
                SELECT email, password_hash, role, is_locked
                FROM users
                WHERE email = :email
                """)
                .param("email", email.strip().toLowerCase(Locale.ROOT))
                .query((row, rowNumber) -> User.withUsername(row.getString("email"))
                        .password(row.getString("password_hash"))
                        .roles(toSecurityRole(row.getString("role")))
                        .accountLocked(row.getBoolean("is_locked"))
                        .build())
                .optional()
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không tồn tại"));
    }

    private String toSecurityRole(String databaseRole) {
        return switch (databaseRole) {
            case "ADMIN" -> "ADMIN";
            case "RECRUITER" -> "PARTNER";
            case "CANDIDATE" -> "STUDENT";
            default -> throw new UsernameNotFoundException("Vai trò tài khoản không hợp lệ");
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService users, PasswordEncoder encoder) {
        var provider = new DaoAuthenticationProvider(users);
        provider.setPasswordEncoder(encoder);
        // Chỉ báo tài khoản bị khóa sau khi mật khẩu đã được xác minh.
        provider.setPreAuthenticationChecks(user -> {});
        provider.setPostAuthenticationChecks(new AccountStatusUserDetailsChecker());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
