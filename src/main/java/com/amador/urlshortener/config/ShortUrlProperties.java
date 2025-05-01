package com.amador.urlshortener.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public record ShortUrlProperties(
    @NotBlank(message = "{validation.baseUrl.notBlank}")
    String baseUrl,

    @Min(1)
    @Max(365)
    @DefaultValue("30")
    int defaultExpiryDays
) {
}
