package de.linkshade.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@ConfigurationProperties(prefix = "app")
@Validated //to be validated before the application starts, ensuring correct values
public record AppProperties(
        int pageDefaultSize,

        @Size(min = 4, max = 6)
        List<Integer> pageAvailableSizes,

        @Min(3)
        int numberOfTries,

        boolean checkHttpStatusCode,

        boolean enableDeleteAccount,

        @Valid
        ShortUrlProperties shortUrlProperties,

        @Valid
        SecurityProperties securityProperties
) {
    public record ShortUrlProperties(
            @NotBlank(message = "{validation.properties.baseUrl.notBlank}")
            String baseUrl,

            @Min(30)
            int defaultExpiryDays,

            @Max(365)
            int maxShortUrlExpirationDays,

            boolean isPrivate,

            @Min(10)
            int defaultShortUrlLength,

            @Max(20)
            int maxShorturlLength,

            @Min(5)
            int minShorturlLength,

            @Min(1000)
            int maxOriginalUrlLength,

            boolean isCustom,

            @Min(6)
            int sharingCodeLength
    ) {
    }

    public record SecurityProperties(
            @Max(10)
            int maxRequestAnonymousUser,

            @Min(20)
            int maxRequestLoggedUser,

            @Min(3)
            int remainingTokensWarning,

            @Min(3)
            int numberCodeTries,

            @Min(15)
            int codeTriesDurationMinutes,

            @Min(1)
            int rateLimitDurationHours,

            @Min(2)
            int bucketsExpirationTimeHours
    ) {
    }
}
