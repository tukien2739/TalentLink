package com.example.talentlink.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
}
