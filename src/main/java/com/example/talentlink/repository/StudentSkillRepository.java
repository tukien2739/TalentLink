package com.example.talentlink.repository;

import java.util.List;

import com.example.talentlink.dto.SkillForm.Category;
import com.example.talentlink.dto.StudentSkillForm;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class StudentSkillRepository {

    private final JdbcClient jdbc;

    public StudentSkillRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<RatedSkill> findOwn(String email) {
        return jdbc.sql("""
                SELECT s.id, s.name, s.category, ps.skill_level AS level
                FROM profile_skills ps JOIN skills s ON s.id = ps.skill_id
                JOIN profiles p ON p.id = ps.profile_id JOIN users u ON u.id = p.user_id
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                ORDER BY s.category, s.name
                """).param("email", email).query(RatedSkill.class).list();
    }

    public int add(String email, StudentSkillForm form) {
        return jdbc.sql("""
                INSERT INTO profile_skills (profile_id, skill_id, skill_level)
                SELECT p.id, s.id, :level
                FROM profiles p JOIN users u ON u.id = p.user_id
                JOIN skills s ON s.id = :skillId AND s.hidden = FALSE
                WHERE u.email = :email AND u.role = 'CANDIDATE'
                """).param("email", email).param("skillId", form.skillId())
                .param("level", form.level()).update();
    }

    public int remove(String email, long skillId) {
        return jdbc.sql("""
                DELETE ps FROM profile_skills ps
                JOIN profiles p ON p.id = ps.profile_id JOIN users u ON u.id = p.user_id
                WHERE u.email = :email AND u.role = 'CANDIDATE' AND ps.skill_id = :skillId
                """).param("email", email).param("skillId", skillId).update();
    }

    public record RatedSkill(long id, String name, Category category, int level) {
    }
}
