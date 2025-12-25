package de.linkshade.web.controllers.dto;

import java.time.LocalDate;

public record ShortUrlEditForm(
        Boolean isPrivate,

        String shortenedUrl,

        LocalDate expirationDate,

        Boolean isRandom,

        Boolean isExpired
) {
}
