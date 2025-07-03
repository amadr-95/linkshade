package de.linkshade.config;

import de.linkshade.util.ValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app")
@Validated //to be validated before the application starts, ensuring correct values
public record AppProperties(
        int pageDefaultSize,
        int[] pageAvailableSizes,
        int numberOfTries,
        @Valid
        ShortUrlProperties shortUrlProperties
) {
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
            int defaultUrlLength,

            boolean isCustom
    ) {
    }
}
