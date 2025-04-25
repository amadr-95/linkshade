package com.amador.urlshortener.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated
public record ShortUrlConfig (
    @NotBlank(message = "{validation.baseUrl.notBlank}")
    @DefaultValue("http://localhost:8080")
    String baseUrl,
    @Positive(message = "{validation.defaultExpiryDays.positive}")
    @Min(1)
    @Max(365)
    @DefaultValue("30")
    int defaultExpiryDays
) {
}
