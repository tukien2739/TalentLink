CREATE TABLE companies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    website_url VARCHAR(2048) NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    company_id BIGINT NULL,
    email VARCHAR(254) COLLATE utf8mb4_0900_as_ci NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) COLLATE utf8mb4_0900_as_cs NOT NULL,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('CANDIDATE', 'RECRUITER', 'ADMIN')),
    CONSTRAINT ck_users_is_locked CHECK (is_locked IN (0, 1)),
    KEY ix_users_company_role (company_id, role),
    CONSTRAINT fk_users_company FOREIGN KEY (company_id)
        REFERENCES companies (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    full_name VARCHAR(150) NOT NULL,
    headline VARCHAR(150) NULL,
    summary TEXT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_profiles_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE educations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    institution VARCHAR(200) NOT NULL,
    degree VARCHAR(100) NULL,
    field_of_study VARCHAR(150) NULL,
    start_date DATE NOT NULL,
    end_date DATE NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_educations_dates CHECK (end_date IS NULL OR end_date >= start_date),
    KEY ix_educations_profile_start (profile_id, start_date DESC, id DESC),
    CONSTRAINT fk_educations_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE skills (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_skills_slug UNIQUE (slug)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profile_skills (
    profile_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,
    skill_level TINYINT NOT NULL,
    PRIMARY KEY (profile_id, skill_id),
    CONSTRAINT ck_profile_skills_level CHECK (skill_level BETWEEN 1 AND 5),
    KEY ix_profile_skills_skill_level (skill_id, skill_level, profile_id),
    CONSTRAINT fk_profile_skills_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_profile_skills_skill FOREIGN KEY (skill_id)
        REFERENCES skills (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT NULL,
    project_url VARCHAR(2048) NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_projects_dates CHECK (start_date IS NULL OR end_date IS NULL OR end_date >= start_date),
    KEY ix_projects_profile_start (profile_id, start_date DESC, id DESC),
    CONSTRAINT fk_projects_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE certificates (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    issuer VARCHAR(200) NOT NULL,
    issued_on DATE NOT NULL,
    expires_on DATE NULL,
    credential_url VARCHAR(2048) NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_certificates_dates CHECK (expires_on IS NULL OR expires_on >= issued_on),
    KEY ix_certificates_profile_issued (profile_id, issued_on DESC, id DESC),
    CONSTRAINT fk_certificates_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE shortlists (
    id BIGINT NOT NULL AUTO_INCREMENT,
    recruiter_user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_shortlists_recruiter_profile UNIQUE (recruiter_user_id, profile_id),
    KEY ix_shortlists_recruiter_created (recruiter_user_id, created_at DESC, id DESC),
    KEY ix_shortlists_profile (profile_id),
    CONSTRAINT fk_shortlists_recruiter FOREIGN KEY (recruiter_user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_shortlists_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE interview_requests (
    id BIGINT NOT NULL AUTO_INCREMENT,
    recruiter_user_id BIGINT NOT NULL,
    profile_id BIGINT NOT NULL,
    status VARCHAR(20) COLLATE utf8mb4_0900_as_cs NOT NULL,
    message TEXT NULL,
    scheduled_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_interview_requests_status
        CHECK (status IN ('PENDING', 'ACCEPTED', 'DECLINED', 'CANCELLED')),
    KEY ix_interview_requests_profile_status (profile_id, status, created_at DESC, id DESC),
    KEY ix_interview_requests_recruiter_created (recruiter_user_id, created_at DESC, id DESC),
    CONSTRAINT fk_interview_requests_recruiter FOREIGN KEY (recruiter_user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_interview_requests_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profile_reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    reviewer_user_id BIGINT NOT NULL,
    decision VARCHAR(20) COLLATE utf8mb4_0900_as_cs NOT NULL,
    note TEXT NULL,
    reviewed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_profile_reviews_decision CHECK (decision IN ('APPROVED', 'REJECTED', 'CHANGES_REQUESTED')),
    KEY ix_profile_reviews_profile_reviewed (profile_id, reviewed_at DESC, id DESC),
    KEY ix_profile_reviews_reviewer_reviewed (reviewer_user_id, reviewed_at DESC, id DESC),
    CONSTRAINT fk_profile_reviews_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_profile_reviews_reviewer FOREIGN KEY (reviewer_user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    read_at DATETIME(6) NULL,
    PRIMARY KEY (id),
    KEY ix_notifications_user_created (user_id, created_at DESC, id DESC),
    KEY ix_notifications_user_read (user_id, read_at, created_at DESC, id DESC),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE profile_views (
    id BIGINT NOT NULL AUTO_INCREMENT,
    profile_id BIGINT NOT NULL,
    viewer_user_id BIGINT NULL,
    viewed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    KEY ix_profile_views_profile_viewed (profile_id, viewed_at),
    KEY ix_profile_views_viewer (viewer_user_id),
    CONSTRAINT fk_profile_views_profile FOREIGN KEY (profile_id)
        REFERENCES profiles (id) ON DELETE RESTRICT ON UPDATE RESTRICT,
    CONSTRAINT fk_profile_views_viewer FOREIGN KEY (viewer_user_id)
        REFERENCES users (id) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
