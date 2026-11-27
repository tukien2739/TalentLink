ALTER TABLE certificates
    ADD COLUMN evidence_key VARCHAR(41) NULL,
    ADD CONSTRAINT uq_certificates_evidence_key UNIQUE (evidence_key);
