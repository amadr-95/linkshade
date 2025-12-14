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
    public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final int SHARING_CODE_LENGTH = 6;

    // Security and rate limit constants
    public static final String X_FORWARDED_FOR ="X-Forwarded-For";
    public static final int REMAINING_TOKENS_WARNING = 3;
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    public static final String DEFAULT_USER_NAME = "Guest";
    public static final int NUMBER_OF_CODE_TRIES = 3;
    public static final String SHARE_CODE_REQUIRED = "share_code_required";
    public static final int CODE_TRIES_DURATION_IN_MINUTES = 1;
    public static final int RATE_LIMIT_DURATION_IN_HOURS = 1;
    public static final int BUCKETS_EXPIRATION_TIME_IN_HOURS = 2;

    // ShortUrlFormFieldNames
    public static final String ORIGINAL_URL = "originalUrl";
    public static final String URL_LENGTH = "urlLength";
    public static final String CUSTOM_URL_NAME = "customShortUrlName";
    public static final String EXPIRATION_DATE = "expirationDate";

    private Constants() {
        // Utility class, no instances
    }
}
