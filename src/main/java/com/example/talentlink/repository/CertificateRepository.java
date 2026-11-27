package com.example.talentlink.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.talentlink.dto.CertificateForm;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class CertificateRepository {

    private static final String ROWS = """
            FROM certificates c JOIN profiles p ON p.id = c.profile_id JOIN users u ON u.id = p.user_id
            """;
    private static final String OWNER = " WHERE u.email = :email AND u.role = 'CANDIDATE'";
    private static final String COLUMNS = "SELECT c.id, c.name, c.issuer, c.issued_on, c.evidence_key, u.email ";
    private static final String ORDER = " ORDER BY c.issued_on DESC, c.id DESC";

    private final JdbcClient jdbc;

    public CertificateRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Certificate> listOwn(String email) {
        return jdbc.sql(COLUMNS + ROWS + OWNER + ORDER).param("email", email).query(Certificate.class).list();
    }

    public List<Certificate> listEvidence() {
        return jdbc.sql(COLUMNS + ROWS + " WHERE c.evidence_key IS NOT NULL" + ORDER)
                .query(Certificate.class).list();
    }

    public Optional<Certificate> findOwn(String email, long id, boolean lock) {
        return jdbc.sql(COLUMNS + ROWS + OWNER + " AND c.id = :id" + (lock ? " FOR UPDATE" : ""))
                .param("email", email).param("id", id).query(Certificate.class).optional();
    }

    public Optional<Certificate> find(long id) {
        return jdbc.sql(COLUMNS + ROWS + " WHERE c.id = :id").param("id", id)
                .query(Certificate.class).optional();
    }

    public int add(String email, CertificateForm form, String key) {
        return jdbc.sql("""
                INSERT INTO certificates (profile_id, name, issuer, issued_on, evidence_key)
                SELECT p.id, :name, :issuer, :issuedOn, :key
                FROM profiles p JOIN users u ON u.id = p.user_id
                """ + OWNER)
                .param("name", form.name().strip()).param("issuer", form.issuer().strip())
                .param("issuedOn", form.issuedOn()).param("key", key).param("email", email).update();
    }

    public void remove(String email, long id) {
        jdbc.sql("DELETE c " + ROWS + OWNER + " AND c.id = :id")
                .param("email", email).param("id", id).update();
    }

    public record Certificate(long id, String name, String issuer, LocalDate issuedOn, String evidenceKey,
            String email) {
    }
}
