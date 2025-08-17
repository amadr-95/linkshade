CREATE SEQUENCE IF NOT EXISTS user_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE short_urls
(
    id                 UUID         NOT NULL,
    shortened_url      VARCHAR(255) NOT NULL,
    original_url       VARCHAR(255) NOT NULL,
    created_by_user    BIGINT,
    is_private         BOOLEAN      NOT NULL,
    created_at         DATE         NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    expires_at         DATE,
    number_of_clicks   BIGINT       NOT NULL,
    CONSTRAINT pk_short_urls PRIMARY KEY (id)
);

CREATE TABLE users
(
    id                 BIGINT                      NOT NULL,
    name               VARCHAR(255)                NOT NULL,
    email              VARCHAR(255)                NOT NULL,
    password           VARCHAR(255)                NOT NULL,
    role               VARCHAR(255)                NOT NULL,
    created_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT emailUnique UNIQUE (email);

ALTER TABLE short_urls
    ADD CONSTRAINT shortUrlUnique UNIQUE (shortened_url);

ALTER TABLE short_urls
    ADD CONSTRAINT FK_USER FOREIGN KEY (created_by_user) REFERENCES users (id);