ALTER TABLE users
    ADD CONSTRAINT emailUnique UNIQUE (email);