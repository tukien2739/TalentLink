ALTER TABLE profiles
    ADD COLUMN visibility VARCHAR(10) COLLATE utf8mb4_0900_as_cs NOT NULL DEFAULT 'PRIVATE',
    ADD CONSTRAINT ck_profiles_visibility CHECK (visibility IN ('PRIVATE', 'PUBLIC'));
