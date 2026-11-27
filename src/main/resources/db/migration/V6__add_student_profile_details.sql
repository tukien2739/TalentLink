ALTER TABLE profiles
    ADD COLUMN birth_date DATE NULL,
    ADD COLUMN phone VARCHAR(16) NULL,
    ADD COLUMN desired_position VARCHAR(150) NULL,
    ADD COLUMN preferred_location VARCHAR(150) NULL;
