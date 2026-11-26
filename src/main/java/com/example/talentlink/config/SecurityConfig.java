package com.example.talentlink.config;

import jakarta.servlet.DispatcherType;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers("/css/talentlink.css", "/webjars/bootstrap/**").permitAll()
                        .requestMatchers("/candidate", "/candidate/**").hasRole("CANDIDATE")
                        .requestMatchers("/recruiter", "/recruiter/**").hasRole("RECRUITER")
                        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .httpBasic(Customizer.withDefaults())
                .logout(logout -> logout.logoutSuccessUrl("/?logout"))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        JdbcDaoImpl users = new JdbcDaoImpl();
        users.setDataSource(dataSource);
        users.setUsersByUsernameQuery("""
                SELECT email, password_hash, NOT is_locked AS enabled
                FROM users
                WHERE email = ?
                """);
        users.setAuthoritiesByUsernameQuery("""
                SELECT email, CONCAT('ROLE_', role) AS authority
                FROM users
                WHERE email = ?
                """);
        return users;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
