package com.example.talentlink.service;

import java.sql.Types;
import java.util.List;
import java.util.Objects;

import com.example.talentlink.dto.PartnerAccountForm;
import com.example.talentlink.exception.ResourceNotFoundException;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAccountService {

    private static final int PAGE_SIZE = 10;
    private static final String DISPLAY_NAME = "COALESCE(p.full_name, u.display_name, c.name)";
    private static final String FILTERED_ACCOUNTS = """
            FROM users u
            LEFT JOIN profiles p ON p.user_id = u.id
            LEFT JOIN companies c ON c.id = u.company_id
            WHERE (:role IS NULL OR u.role = :role)
              AND (:locked IS NULL OR u.is_locked = :locked)
              AND (u.email LIKE :term ESCAPE '!' OR %s LIKE :term ESCAPE '!')
            """.formatted(DISPLAY_NAME);

    private final JdbcClient jdbc;
    private final PasswordEncoder encoder;

    public AdminAccountService(JdbcClient jdbc, PasswordEncoder encoder) {
        this.jdbc = jdbc;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public AccountPage search(String query, Role role, State state, long requestedPage) {
        String term = "%" + query.replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
        long total = filteredQuery("SELECT COUNT(*) ", term, role, state, "").query(Long.class).single();
        long lastPage = Math.max(1, total / PAGE_SIZE + (total % PAGE_SIZE == 0 ? 0 : 1));
        long page = Math.min(Math.max(1, requestedPage), lastPage);
        var rows = filteredQuery("SELECT u.id, u.email, u.role, u.is_locked, c.name AS company, "
                        + DISPLAY_NAME + " AS display_name ", term, role, state, "ORDER BY u.id DESC LIMIT :size OFFSET :offset")
                .param("size", PAGE_SIZE)
                .param("offset", (page - 1) * PAGE_SIZE)
                .query((row, rowNumber) -> new AccountRow(row.getLong("id"), row.getString("email"),
                        row.getString("display_name"), row.getString("company"),
                        Role.valueOf(row.getString("role")), row.getBoolean("is_locked")))
                .list();
        return new AccountPage(rows, page, lastPage, total);
    }

    private JdbcClient.StatementSpec filteredQuery(String select, String term, Role role, State state, String tail) {
        return jdbc.sql(select + FILTERED_ACCOUNTS + tail)
                .param("term", term)
                .param("role", role == null ? null : role.name(), Types.VARCHAR)
                .param("locked", state == null ? null : state == State.LOCKED, Types.BOOLEAN);
    }

    @Transactional
    public void createPartner(PartnerAccountForm form) {
        String passwordHash = encoder.encode(form.password());
        var companyKey = new GeneratedKeyHolder();
        jdbc.sql("INSERT INTO companies (name, created_at) VALUES (:name, UTC_TIMESTAMP(6))")
                .param("name", form.companyName())
                .update(companyKey, "id");
        long companyId = Objects.requireNonNull(companyKey.getKey(), "Missing generated company ID").longValue();
        try {
            jdbc.sql("""
                    INSERT INTO users (company_id, display_name, email, password_hash, role, created_at)
                    VALUES (:companyId, :name, :email, :hash, 'RECRUITER', UTC_TIMESTAMP(6))
                    """)
                    .param("companyId", companyId)
                    .param("name", form.fullName())
                    .param("email", form.email())
                    .param("hash", passwordHash)
                    .update();
        } catch (DuplicateKeyException e) {
            throw new PartnerEmailAlreadyUsedException(e);
        }
    }

    public AccountTarget confirmStatus(long id, boolean locked, String actorEmail) {
        return statusTarget(id, locked, actorEmail, false);
    }

    @Transactional
    public void setLocked(long id, boolean locked, String actorEmail) {
        statusTarget(id, locked, actorEmail, true);
        jdbc.sql("UPDATE users SET is_locked = :locked WHERE id = :id")
                .param("id", id)
                .param("locked", locked)
                .update();
    }

    private AccountTarget statusTarget(long id, boolean locked, String actorEmail, boolean forUpdate) {
        var target = jdbc.sql("SELECT email FROM users WHERE id = :id" + (forUpdate ? " FOR UPDATE" : ""))
                .param("id", id)
                .query(String.class)
                .optional()
                .orElseThrow(() -> new ResourceNotFoundException("Account does not exist"));
        if (locked && target.equalsIgnoreCase(actorEmail)) {
            throw new AccessDeniedException("Administrators cannot lock their own account");
        }
        return new AccountTarget(id, target);
    }

    public enum Role {
        ADMIN("Quản trị viên"), RECRUITER("Đối tác"), CANDIDATE("Sinh viên");

        private final String label;

        Role(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public enum State {
        ACTIVE("Hoạt động"), LOCKED("Đã khóa");

        private final String label;

        State(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public record AccountRow(long id, String email, String displayName, String company, Role role, boolean locked) {}

    public record AccountPage(List<AccountRow> rows, long number, long lastPage, long total) {}

    public record AccountTarget(long id, String email) {}

    public static class PartnerEmailAlreadyUsedException extends RuntimeException {
        private PartnerEmailAlreadyUsedException(DuplicateKeyException cause) {
            super(cause);
        }
    }
}
