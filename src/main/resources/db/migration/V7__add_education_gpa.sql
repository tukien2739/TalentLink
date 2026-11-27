ALTER TABLE educations
    ADD COLUMN gpa DECIMAL(5,4) NULL,
    ADD CONSTRAINT ck_educations_gpa CHECK (gpa BETWEEN 0 AND 4);
