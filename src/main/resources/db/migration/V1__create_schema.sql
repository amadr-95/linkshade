CREATE TABLE short_urls
(
    id               UUID                     NOT NULL,
    shortened_url    VARCHAR(50)              NOT NULL,
    original_url     TEXT                     NOT NULL,
    created_by_user  UUID,
    is_private       BOOLEAN                  NOT NULL,
    share_code       VARCHAR(25),
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    last_modified_at TIMESTAMP WITH TIME ZONE,
    expires_at       DATE,
    zone_id          VARCHAR(50)              NOT NULL,
    number_of_clicks BIGINT                   NOT NULL,
    CONSTRAINT pk_short_urls PRIMARY KEY (id)
);

CREATE TABLE users
(
    id               UUID                     NOT NULL,
    name             VARCHAR(50)              NOT NULL,
    email            VARCHAR(255)             NOT NULL,
    auth_provider    VARCHAR(50)              NOT NULL,
    user_provider_id VARCHAR(50)              NOT NULL,
    role             VARCHAR(50)              NOT NULL,
    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE users
    ADD CONSTRAINT emailUnique UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT providerIdUnique UNIQUE (user_provider_id);

ALTER TABLE short_urls
    ADD CONSTRAINT shortUrlUnique UNIQUE (shortened_url);

ALTER TABLE short_urls
    ADD CONSTRAINT fk_user FOREIGN KEY (created_by_user) REFERENCES users (id);

-- Index for user lookups
CREATE INDEX idx_short_urls_user ON short_urls (created_by_user);

-- Index for expiration cleanup/reactivation
CREATE INDEX idx_short_urls_user_expiry ON short_urls (created_by_user, expires_at)
    WHERE created_by_user IS NOT NULL AND expires_at IS NOT NULL;

-- Index for batch deleting when urls are expired
CREATE INDEX idx_short_urls_null_user_expiry ON short_urls (expires_at)
    WHERE created_by_user IS NULL AND expires_at IS NOT NULL;