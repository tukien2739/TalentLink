package com.example.talentlink.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class ProfilePreviewRepository {

    private final JdbcClient jdbc;

    public ProfilePreviewRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Candidate> findOwnCandidate(String principalName) {
        return jdbc.sql("""
                SELECT p.id, p.full_name, p.headline, p.summary, p.birth_date,
                       p.desired_position, p.preferred_location, p.avatar_data IS NOT NULL AS has_avatar
                FROM profiles p JOIN users u ON u.id = p.user_id
                WHERE u.email = :principalName AND u.role = 'CANDIDATE'
                """).param("principalName", principalName).query(Candidate.class).optional();
    }

    public List<CertificateSummary> findCertificates(long profileId) {
        return jdbc.sql("""
                SELECT name, issuer, issued_on, expires_on
                FROM certificates WHERE profile_id = :profileId
                ORDER BY issued_on DESC, id DESC
                """).param("profileId", profileId).query(CertificateSummary.class).list();
    }

    public record Candidate(long id, String fullName, String headline, String summary, LocalDate birthDate,
            String desiredPosition, String preferredLocation, boolean hasAvatar) {
    }

    public record CertificateSummary(String name, String issuer, LocalDate issuedOn, LocalDate expiresOn) {
    }
}
