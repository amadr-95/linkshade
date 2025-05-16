package com.amador.urlshortener.config;

import com.amador.urlshortener.util.ValidationConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "short-url")
@Validated //to be validated before the application starts, ensuring correct values
public record ShortUrlProperties(
    @NotBlank(message = "{validation.baseUrl.notBlank}")
    String baseUrl,
    @Min(
            value = ValidationConstants.MIN_URL_EXPIRATION_DAYS,
            message = "{validation.defaultExpiryDays.range}")
    @Max(
            value = ValidationConstants.MAX_URL_EXPIRATION_DAYS,
            message = "{validation.defaultExpiryDays.range}")
    int defaultExpiryDays,

    boolean isPrivate,

    @Min(
            value = ValidationConstants.MIN_URL_LENGTH,
            message = "{validation.urlLength.range}")
    @Max(
            value = ValidationConstants.MAX_URL_LENGTH,
            message = "{validation.urlLength.range}")
    int urlLength
) {
}
