package com.example.talentlink.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

import com.example.talentlink.dto.EducationForm;
import com.example.talentlink.exception.ResourceNotFoundException;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
public class StudentEducationService {

    private final JdbcClient jdbc;

    public StudentEducationService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Education> listOwn(String email) {
        return jdbc.sql("""
                SELECT e.id, e.institution, e.field_of_study, e.start_date, e.end_date, e.gpa
                FROM educations e
                JOIN profiles p ON p.id = e.profile_id
                JOIN users u ON u.id = p.user_id
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                ORDER BY e.start_date DESC, e.id DESC
                """)
                .param("email", email)
                .query(this::mapEducation)
                .list();
    }

    public Education findOwn(String email, long id) {
        return jdbc.sql("""
                SELECT e.id, e.institution, e.field_of_study, e.start_date, e.end_date, e.gpa, u.email
                FROM educations e
                JOIN profiles p ON p.id = e.profile_id
                JOIN users u ON u.id = p.user_id
                WHERE e.id = :id
                """)
                .param("id", id)
                .query((row, rowNumber) -> {
                    if (!email.equals(row.getString("email"))) {
                        throw new AccessDeniedException("Education belongs to another profile");
                    }
                    return mapEducation(row, rowNumber);
                })
                .optional()
                .orElseThrow(() -> new ResourceNotFoundException("Education does not exist"));
    }

    public void create(String email, EducationForm form) {
        int inserted = bindForm(jdbc.sql("""
                INSERT INTO educations (profile_id, institution, field_of_study, start_date, end_date, gpa)
                SELECT p.id, :institution, :fieldOfStudy, :startDate, :endDate, :gpa
                FROM profiles p JOIN users u ON u.id = p.user_id
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                """), form)
                .param("email", email)
                .update();
        if (inserted == 0) {
            throw new ResourceNotFoundException("Student profile does not exist");
        }
    }

    public void update(String email, long id, EducationForm form) {
        int updated = bindForm(jdbc.sql("""
                UPDATE educations e JOIN profiles p ON p.id = e.profile_id JOIN users u ON u.id = p.user_id
                SET e.institution = :institution, e.field_of_study = :fieldOfStudy,
                    e.start_date = :startDate, e.end_date = :endDate, e.gpa = :gpa
                WHERE e.id = :id AND u.email = :email AND u.role = 'CANDIDATE'
                """), form)
                .param("email", email)
                .param("id", id)
                .update();
        if (updated == 0) {
            findOwn(email, id);
        }
    }

    public void delete(String email, long id) {
        int deleted = jdbc.sql("""
                DELETE e FROM educations e JOIN profiles p ON p.id = e.profile_id JOIN users u ON u.id = p.user_id
                WHERE e.id = :id AND u.email = :email AND u.role = 'CANDIDATE'
                """)
                .param("email", email)
                .param("id", id)
                .update();
        if (deleted == 0) {
            findOwn(email, id);
        }
    }

    private JdbcClient.StatementSpec bindForm(JdbcClient.StatementSpec statement, EducationForm form) {
        LocalDate end = form.endDate() == null ? null : LocalDate.parse(form.endDate());
        BigDecimal gpa = form.gpa() == null ? null : new BigDecimal(form.gpa().replace(',', '.'));
        return statement.param("institution", form.institution())
                .param("fieldOfStudy", form.fieldOfStudy())
                .param("startDate", LocalDate.parse(form.startDate()), Types.DATE)
                .param("endDate", end, Types.DATE)
                .param("gpa", gpa, Types.DECIMAL);
    }

    private Education mapEducation(ResultSet row, int rowNumber) throws SQLException {
        BigDecimal gpa = row.getBigDecimal("gpa");
        var form = new EducationForm(row.getString("institution"), row.getString("field_of_study"),
                row.getString("start_date"), row.getString("end_date"),
                gpa == null ? null : gpa.stripTrailingZeros().toPlainString());
        return new Education(row.getLong("id"), form);
    }

    public record Education(long id, EducationForm form) {}
}
