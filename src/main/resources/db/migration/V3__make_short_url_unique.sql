ALTER TABLE short_urls
    ADD CONSTRAINT shortUrlUnique UNIQUE (shortened_url);

DROP SEQUENCE user_seq CASCADE;