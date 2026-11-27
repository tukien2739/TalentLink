package com.example.talentlink.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.example.talentlink.dto.SkillForm;
import com.example.talentlink.dto.SkillForm.Category;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class SkillRepository {

    private final JdbcClient jdbc;

    public SkillRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Skill> findAll() {
        return jdbc.sql("SELECT id, name, category, hidden FROM skills ORDER BY hidden, category, name")
                .query(Skill.class).list();
    }

    public List<Skill> findVisible() {
        return jdbc.sql("SELECT id, name, category, hidden FROM skills WHERE hidden = FALSE ORDER BY name")
                .query(Skill.class).list();
    }

    public Optional<Skill> findById(long id) {
        return jdbc.sql("SELECT id, name, category, hidden FROM skills WHERE id = :id")
                .param("id", id).query(Skill.class).optional();
    }

    public void create(SkillForm form) {
        jdbc.sql("INSERT INTO skills (name, slug, category) VALUES (:name, :slug, :category)")
                .param("name", form.name())
                .param("slug", UUID.randomUUID().toString())
                .param("category", form.category().name())
                .update();
    }

    public int update(long id, SkillForm form) {
        return jdbc.sql("UPDATE skills SET name = :name, category = :category WHERE id = :id")
                .param("name", form.name())
                .param("category", form.category().name())
                .param("id", id).update();
    }

    public int hide(long id) {
        return jdbc.sql("UPDATE skills SET hidden = TRUE WHERE id = :id")
                .param("id", id).update();
    }

    public record Skill(long id, String name, Category category, boolean hidden) {
    }
}
