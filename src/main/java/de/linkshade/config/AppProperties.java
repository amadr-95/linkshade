package de.linkshade.config;

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
        boolean checkHttpStatusCode,
        int maxRequestAnonymousUser,
        int maxRequestLoggedUser,
        boolean enableDeleteAccount,
        @Valid
        ShortUrlProperties shortUrlProperties
) {
    public record ShortUrlProperties(
            @NotBlank(message = "{validation.properties.baseUrl.notBlank}")
            String baseUrl,
            @Min(
                    value = Constants.SHORTURL_EXPIRY_DAYS_DEFAULT,
                    message = "{validation.properties.defaultExpiryDays}")
            @Max(
                    value = Constants.SHORTURL_EXPIRY_DAYS_DEFAULT,
                    message = "{validation.properties.defaultExpiryDays}")
            int defaultExpiryDays,

            boolean isPrivate,

            @Min(
                    value = Constants.SHORTURL_LENGTH_DEFAULT,
                    message = "{validation.properties.shortUrlLength.default}")
            @Max(
                    value = Constants.SHORTURL_LENGTH_DEFAULT,
                    message = "{validation.properties.shortUrlLength.default}")
            int defaultShortUrlLength,

            boolean isCustom
    ) {
    }
}
