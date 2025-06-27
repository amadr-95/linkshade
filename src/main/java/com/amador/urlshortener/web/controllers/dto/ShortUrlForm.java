package com.amador.urlshortener.web.controllers.dto;

import com.amador.urlshortener.util.annotations.ValidForm;

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
