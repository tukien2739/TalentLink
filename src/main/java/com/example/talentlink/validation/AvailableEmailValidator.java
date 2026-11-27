package com.example.talentlink.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.jdbc.core.simple.JdbcClient;

public class AvailableEmailValidator implements ConstraintValidator<AvailableEmail, String> {

    private final JdbcClient jdbc;

    public AvailableEmailValidator(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank() || email.length() > 254) {
            return true;
        }
        return jdbc.sql("SELECT 1 FROM users WHERE email = :email")
                .param("email", email)
                .query(Integer.class)
                .optional()
                .isEmpty();
    }
}
