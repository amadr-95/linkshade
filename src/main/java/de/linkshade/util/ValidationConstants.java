package de.linkshade.util;

import java.util.regex.Pattern;

public final class ValidationConstants {

    public static final int MIN_URL_EXPIRATION_DAYS = 0;
    public static final int MAX_URL_EXPIRATION_DAYS = 365;
    public static final int MIN_URL_LENGTH = 5;
    public static final int MAX_URL_LENGTH = 20;
    public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    public static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$");

    private ValidationConstants() {
        // Utility class, no instances
    }
}
