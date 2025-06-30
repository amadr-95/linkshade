package com.amador.linkshade.web.controllers.dto;

import com.amador.linkshade.util.annotations.ValidForm;

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
