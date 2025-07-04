package de.linkshade.web.controllers.dto;

import de.linkshade.util.annotations.ValidUrlForm;

@ValidUrlForm
public record ShortUrlForm(
        String originalUrl,

        Integer expirationInDays,

        Boolean isPrivate,

        Integer urlLength,

        Boolean isCustom,

        String customShortUrlName
) {
}
