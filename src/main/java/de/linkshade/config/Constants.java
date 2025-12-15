package de.linkshade.config;

import java.util.Set;
import java.util.regex.Pattern;

public final class Constants {

    // ShorUrlProperties constants
    public static final Set<String> URL_SORT_PROPERTIES =
            Set.of("shortenedUrl", "originalUrl", "numberOfClicks", "createdByUser", "isPrivate", "createdAt", "expiresAt");
    public static final Set<String> USER_SORT_PROPERTIES =
            Set.of("id", "name", "email", "authProvider", "userProviderId", "numberOfUrlsCreated", "createdAt");

    // ShortUrlForm constants
    public static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?|ftp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    public static final String VALID_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    // Security and rate limit constants
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String ANONYMOUS_USER_NAME = "anonymousUser";
    public static final String DEFAULT_USER_NAME = "Guest";
    public static final String SHARE_CODE_REQUIRED = "share_code_required";

    // ShortUrlFormFieldNames
    public static final String ORIGINAL_URL = "originalUrl";
    public static final String URL_LENGTH = "urlLength";
    public static final String CUSTOM_URL_NAME = "customShortUrlName";
    public static final String EXPIRATION_DATE = "expirationDate";

    private Constants() {
        // Utility class, no instances
    }
}
