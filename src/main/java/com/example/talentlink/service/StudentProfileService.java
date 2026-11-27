package com.example.talentlink.service;

import java.sql.Types;
import java.time.LocalDate;

import com.example.talentlink.dto.StudentProfileForm;
import com.example.talentlink.exception.ResourceNotFoundException;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

@Service
public class StudentProfileService {

    private final JdbcClient jdbc;

    public StudentProfileService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public StudentProfileForm findOwnProfile(String email) {
        return jdbc.sql("""
                SELECT p.full_name, p.birth_date, p.phone, p.summary, p.desired_position, p.preferred_location
                FROM profiles p JOIN users u ON u.id = p.user_id
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                """)
                .param("email", email)
                .query((row, rowNumber) -> new StudentProfileForm(row.getString("full_name"),
                        row.getString("birth_date"), row.getString("phone"), row.getString("summary"),
                        row.getString("desired_position"), row.getString("preferred_location")))
                .optional()
                .orElseThrow(() -> new ResourceNotFoundException("Student profile does not exist"));
    }

    public void updateOwnProfile(String email, StudentProfileForm form) {
        LocalDate birthDate = form.birthDate() == null ? null : LocalDate.parse(form.birthDate());
        int updated = jdbc.sql("""
                UPDATE profiles p JOIN users u ON u.id = p.user_id
                SET p.full_name = :fullName, p.birth_date = :birthDate, p.phone = :phone,
                    p.summary = :summary, p.desired_position = :desiredPosition,
                    p.preferred_location = :preferredLocation, p.updated_at = UTC_TIMESTAMP(6)
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                """)
                .param("email", email)
                .param("fullName", form.fullName())
                .param("birthDate", birthDate, Types.DATE)
                .param("phone", form.phone(), Types.VARCHAR)
                .param("summary", form.summary(), Types.VARCHAR)
                .param("desiredPosition", form.desiredPosition(), Types.VARCHAR)
                .param("preferredLocation", form.preferredLocation(), Types.VARCHAR)
                .update();
        if (updated == 0) {
            throw new ResourceNotFoundException("Student profile does not exist");
        }
    }
}
