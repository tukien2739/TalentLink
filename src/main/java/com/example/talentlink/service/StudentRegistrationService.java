package com.example.talentlink.service;

import java.util.Objects;

import com.example.talentlink.dto.StudentRegistrationForm;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StudentRegistrationService {

    private final JdbcClient jdbc;
    private final PasswordEncoder passwordEncoder;

    public StudentRegistrationService(JdbcClient jdbc, PasswordEncoder passwordEncoder) {
        this.jdbc = jdbc;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        return jdbc.sql("SELECT 1 FROM users WHERE email = :email")
                .param("email", email)
                .query(Integer.class)
                .optional()
                .isPresent();
    }

    @Transactional
    public void register(StudentRegistrationForm form) {
        var userKey = new GeneratedKeyHolder();
        String passwordHash = passwordEncoder.encode(form.password());
        try {
            jdbc.sql("""
                    INSERT INTO users (email, password_hash, role, created_at)
                    VALUES (:email, :passwordHash, 'CANDIDATE', UTC_TIMESTAMP(6))
                    """)
                    .param("email", form.email())
                    .param("passwordHash", passwordHash)
                    .update(userKey, "id");
        } catch (DuplicateKeyException e) {
            throw new EmailAlreadyUsedException(e);
        }

        long userId = Objects.requireNonNull(userKey.getKey(), "Missing generated user ID").longValue();
        jdbc.sql("""
                INSERT INTO profiles (user_id, full_name, visibility, created_at, updated_at)
                VALUES (:userId, :fullName, 'PRIVATE', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))
                """)
                .param("userId", userId)
                .param("fullName", form.fullName())
                .update();
    }

    public static class EmailAlreadyUsedException extends RuntimeException {

        private EmailAlreadyUsedException(DuplicateKeyException cause) {
            super(cause);
        }
    }
}
