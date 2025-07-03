package de.linkshade.util;

public final class ValidationConstants {

    public static final int MIN_URL_EXPIRATION_DAYS = 1;
    public static final int MAX_URL_EXPIRATION_DAYS = 365;
    public static final int MIN_URL_LENGTH = 5;
    public static final int MAX_URL_LENGTH = 20;
    public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private ValidationConstants() {
        // Utility class, no instances
    }
}
