package de.linkshade.util;

import java.util.regex.Pattern;

public final class Constants {

    public static final String X_FORWARDED_FOR ="X-Forwarded-For";
    public static final int CONSECUTIVE_ERRORS_ALLOWED = 3;
    public static final int REMAINING_TOKENS_WARNING = 3;
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    public static final String DEFAULT_USER_NAME = "Guest";
    public static final int MIN_URL_EXPIRATION_DAYS = 0;
    public static final int MAX_URL_EXPIRATION_DAYS = 365;
    public static final int MIN_URL_LENGTH = 5;
    public static final int MAX_URL_LENGTH = 20;
    public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    private Constants() {
        // Utility class, no instances
    }
}
