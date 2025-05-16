package com.amador.urlshortener.web.controllers.dto;

import com.amador.urlshortener.util.ValidationConstants;
import com.amador.urlshortener.util.annotations.ValidUrl;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ShortUrlForm(
        @ValidUrl
        String originalUrl,

        @Min(
                value = ValidationConstants.MIN_URL_EXPIRATION_DAYS,
                message = "{validation.defaultExpiryDays.range}")
        @Max(
                value = ValidationConstants.MAX_URL_EXPIRATION_DAYS,
                message = "{validation.defaultExpiryDays.range}")
        Integer expirationInDays,

        Boolean isPrivate,

        @Min(
                value = ValidationConstants.MIN_URL_LENGTH,
                message = "{validation.urlLength.range}")
        @Max(
                value = ValidationConstants.MAX_URL_LENGTH,
                message = "{validation.urlLength.range}")
        Integer urlLength
) {
}
