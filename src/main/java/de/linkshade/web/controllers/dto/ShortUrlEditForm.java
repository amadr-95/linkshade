package de.linkshade.web.controllers.dto;

public record ShortUrlEditForm(
        Boolean isPrivate,

        String shortenedUrl,

        Integer daysToExpire,

        Boolean isRandom,

        Boolean isExpired
) {
}
