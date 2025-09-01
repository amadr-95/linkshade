package de.linkshade.domain.entities;

public enum AuthProvider {
    GOOGLE,
    GITHUB,
    NOT_SUPPORTED;

    public static AuthProvider from(String provider) {
        return switch (provider.toLowerCase()) {
            case "github" -> GITHUB;
            case "google" -> GOOGLE;
            default -> NOT_SUPPORTED;
        };
    }
}
