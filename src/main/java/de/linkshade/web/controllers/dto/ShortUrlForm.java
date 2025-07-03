package de.linkshade.web.controllers.dto;

import de.linkshade.util.annotations.ValidForm;

@ValidForm
public record ShortUrlForm(
        String originalUrl,

        Integer expirationInDays,

        Boolean isPrivate,

        Integer urlLength,

        Boolean isCustom,

        String customShortUrlName
) {
}
