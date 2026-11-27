package com.example.talentlink.service;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountPasswordService {

    private static final int MAX_FAILED_ATTEMPTS = 5;

    private final JdbcClient jdbc;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();

    public AccountPasswordService(JdbcClient jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Transactional
    public boolean change(String email, String currentPassword, String newPassword) {
        var account = lockActiveAccount(email);
        if (account.isEmpty() || !encoder.matches(currentPassword, account.get().passwordHash())) {
            return false;
        }
        replacePassword(account.get().id(), newPassword);
        return true;
    }

    @Transactional
    public Optional<String> issueResetCode(String email) {
        var account = lockActiveAccount(email);
        if (account.isEmpty()) {
            return Optional.empty();
        }
        long userId = account.get().id();
        boolean recentlyIssued = jdbc.sql("""
                SELECT 1 FROM password_reset_codes
                WHERE user_id = :id AND issued_at > UTC_TIMESTAMP(6) - INTERVAL 60 SECOND
                """).param("id", userId).query(Integer.class).optional().isPresent();
        if (recentlyIssued) {
            return Optional.empty();
        }

        String code = String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
        jdbc.sql("""
                INSERT INTO password_reset_codes (user_id, code_hash, issued_at, expires_at)
                VALUES (:id, :hash, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE)
                ON DUPLICATE KEY UPDATE code_hash = :hash, issued_at = UTC_TIMESTAMP(6),
                    expires_at = UTC_TIMESTAMP(6) + INTERVAL 10 MINUTE, failed_attempts = 0
                """)
                .param("id", userId)
                .param("hash", encoder.encode(code))
                .update();
        return Optional.of(code);
    }

    @Transactional
    public boolean reset(String email, String code, String newPassword) {
        var account = lockActiveAccount(email);
        if (account.isEmpty()) {
            return false;
        }
        long userId = account.get().id();
        var codeHash = jdbc.sql("""
                SELECT code_hash FROM password_reset_codes
                WHERE user_id = :id AND expires_at > UTC_TIMESTAMP(6) AND failed_attempts < :maxAttempts
                """)
                .param("id", userId)
                .param("maxAttempts", MAX_FAILED_ATTEMPTS)
                .query(String.class)
                .optional();
        if (codeHash.isEmpty()) {
            return false;
        }
        if (!encoder.matches(code, codeHash.get())) {
            jdbc.sql("UPDATE password_reset_codes SET failed_attempts = failed_attempts + 1 WHERE user_id = :id")
                    .param("id", userId)
                    .update();
            return false;
        }
        replacePassword(userId, newPassword);
        return true;
    }

    private Optional<Account> lockActiveAccount(String email) {
        // Serialize changes and reset requests for the same account.
        return jdbc.sql("SELECT id, password_hash FROM users WHERE email = :email AND is_locked = FALSE FOR UPDATE")
                .param("email", email)
                .query((row, rowNumber) -> new Account(row.getLong("id"), row.getString("password_hash")))
                .optional();
    }

    private void replacePassword(long userId, String newPassword) {
        jdbc.sql("UPDATE users SET password_hash = :hash WHERE id = :id")
                .param("id", userId)
                .param("hash", encoder.encode(newPassword))
                .update();
        jdbc.sql("DELETE FROM password_reset_codes WHERE user_id = :id").param("id", userId).update();
    }

    private record Account(long id, String passwordHash) {}
}
