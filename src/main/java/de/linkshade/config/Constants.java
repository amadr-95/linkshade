package de.linkshade.config;

import java.util.regex.Pattern;

public final class Constants {

    // ShorUrlProperties constants
    public static final int SHORTURL_EXPIRY_DAYS_DEFAULT = 30;
    public static final int SHORTURL_LENGTH_DEFAULT = 10;

    // ShortUrlForm constants
    public static final int MAX_ORIGINAL_URL_LENGTH = 1000;
    public static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    public static final int MIN_SHORTURL_LENGTH = 5;
    public static final int MAX_SHORTURL_LENGTH = 20;
    public static final int MAX_SHORTURL_EXPIRATION_DAYS = 365;
    public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0123456789";

    // Security and rate limit constants
    public static final String X_FORWARDED_FOR ="X-Forwarded-For";
    public static final int REMAINING_TOKENS_WARNING = 3;
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    public static final String DEFAULT_USER_NAME = "Guest";

    private Constants() {
        // Utility class, no instances
    }
}
