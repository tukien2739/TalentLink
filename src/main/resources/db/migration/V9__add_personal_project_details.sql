ALTER TABLE projects
    ADD COLUMN project_role VARCHAR(200),
    ADD COLUMN technologies VARCHAR(500),
    ADD COLUMN github_url VARCHAR(2048),
    ADD COLUMN demo_url VARCHAR(2048);
