package de.linkshade.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthProvider {
    GOOGLE("sub", "given_name", "picture"),
    GITHUB("id", "name", "avatar_url"),
    NOT_SUPPORTED(null, null, null);

    private final String idKey;
    private final String nameKey;
    private final String avatarKey;
    public final static String EMAIL = "email"; // same key for both Google and GitHub

    public static AuthProvider from(String provider) {
        if (provider == null) return NOT_SUPPORTED;
        return switch (provider.toLowerCase()) {
            case "github" -> GITHUB;
            case "google" -> GOOGLE;
            default -> NOT_SUPPORTED;
        };
    }
}
