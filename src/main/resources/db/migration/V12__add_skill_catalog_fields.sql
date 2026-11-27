ALTER TABLE skills
    MODIFY name VARCHAR(100) COLLATE utf8mb4_0900_as_ci NOT NULL,
    ADD CONSTRAINT uq_skills_name UNIQUE (name),
    ADD COLUMN category VARCHAR(20) NOT NULL,
    ADD COLUMN hidden BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE skills
SET category = CASE
    WHEN slug IN ('java', 'javascript', 'python', 'html', 'css', 'kotlin', 'swift', 'c-sharp', 'dart', 'cpp')
        THEN 'LANGUAGE'
    WHEN slug IN ('spring-boot', 'react', 'pandas', 'android', 'swiftui', 'junit', 'dotnet', 'scikit-learn', 'numpy', 'flutter')
        THEN 'FRAMEWORK'
    WHEN slug IN ('sql', 'mysql') THEN 'DATABASE'
    ELSE 'TOOL'
END;

ALTER TABLE skills
    ADD CONSTRAINT ck_skills_category CHECK (category IN ('LANGUAGE', 'FRAMEWORK', 'DATABASE', 'TOOL', 'SOFT_SKILL'));
