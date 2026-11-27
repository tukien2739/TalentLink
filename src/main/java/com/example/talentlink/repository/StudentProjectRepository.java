package com.example.talentlink.repository;

import java.util.List;
import java.util.Optional;

import com.example.talentlink.dto.StudentProjectForm;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class StudentProjectRepository {

    private static final String OWN_PROFILE = """
            FROM profiles p JOIN users u ON u.id = p.user_id
            WHERE u.email = :email AND u.role = 'CANDIDATE'
            """;

    private static final String OWN_PROJECTS = """
            FROM projects j JOIN profiles p ON p.id = j.profile_id JOIN users u ON u.id = p.user_id
            WHERE u.email = :email AND u.role = 'CANDIDATE'
            """;

    private static final String COLUMNS = "SELECT j.id, j.name, j.project_role, j.technologies, "
            + "j.github_url, j.demo_url, j.description ";
    private final JdbcClient jdbc;

    public StudentProjectRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<Project> findOwn(String email) {
        return jdbc.sql(COLUMNS + OWN_PROJECTS + " ORDER BY j.id DESC")
                .param("email", email).query(Project.class).list();
    }

    public Optional<Project> findOwn(String email, long id) {
        return jdbc.sql(COLUMNS + OWN_PROJECTS + " AND j.id = :id")
                .param("email", email).param("id", id).query(Project.class).optional();
    }

    @Transactional
    public boolean add(String email, StudentProjectForm form) {
        // Serialize additions to the same profile before checking its project count.
        long profileId = jdbc.sql("SELECT p.id " + OWN_PROFILE + " FOR UPDATE")
                .param("email", email).query(Long.class).single();
        long count = jdbc.sql("SELECT COUNT(*) FROM projects WHERE profile_id = :profileId")
                .param("profileId", profileId).query(Long.class).single();
        if (count >= 20) {
            return false;
        }
        fields(jdbc.sql("""
                INSERT INTO projects (profile_id, name, project_role, technologies, github_url, demo_url, description)
                VALUES (:profileId, :name, :role, :technologies, :githubUrl, :demoUrl, :description)
                """), form).param("profileId", profileId).update();
        return true;
    }

    public int update(String email, long id, StudentProjectForm form) {
        return fields(jdbc.sql("""
                UPDATE projects j JOIN profiles p ON p.id = j.profile_id JOIN users u ON u.id = p.user_id
                SET j.name = :name, j.project_role = :role, j.technologies = :technologies,
                    j.github_url = :githubUrl, j.demo_url = :demoUrl, j.description = :description
                WHERE u.email = :email AND u.role = 'CANDIDATE' AND j.id = :id
                """), form).param("email", email).param("id", id).update();
    }

    public int remove(String email, long id) {
        return jdbc.sql("DELETE j " + OWN_PROJECTS + " AND j.id = :id")
                .param("email", email).param("id", id).update();
    }

    private JdbcClient.StatementSpec fields(JdbcClient.StatementSpec statement, StudentProjectForm form) {
        return statement.param("name", form.name()).param("role", form.projectRole())
                .param("technologies", form.technologies()).param("githubUrl", form.githubUrl())
                .param("demoUrl", form.demoUrl()).param("description", form.description());
    }

    public record Project(long id, String name, String projectRole, String technologies,
            String githubUrl, String demoUrl, String description) {

        public StudentProjectForm form() {
            return new StudentProjectForm(name, projectRole, technologies, githubUrl, demoUrl, description);
        }

        public boolean hasGithubLink() {
            return StudentProjectForm.validUrl(githubUrl, true);
        }

        public boolean hasDemoLink() {
            return StudentProjectForm.validUrl(demoUrl, false);
        }
    }
}
