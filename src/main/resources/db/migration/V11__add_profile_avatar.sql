ALTER TABLE profiles
    ADD COLUMN avatar_data MEDIUMBLOB NULL,
    ADD COLUMN avatar_content_type VARCHAR(10) NULL;
