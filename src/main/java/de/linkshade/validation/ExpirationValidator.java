package de.linkshade.validation;

import de.linkshade.config.AppProperties;
import de.linkshade.config.Constants;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirationValidator {

    private final ValidationContextBuilder contextBuilder;
    private final AppProperties appProperties;

    public boolean isValid(ConstraintValidatorContext context, LocalDate expirationDate) {
        LocalDate now = LocalDate.now();
        if (expirationDate.isBefore(now) ||
                expirationDate.isAfter(now.plusDays(appProperties.shortUrlProperties().maxShortUrlExpirationDays()))) {
            contextBuilder.buildContext(context, "{validation.urlForm.expirationDate}", Constants.EXPIRATION_DATE);
            return false;
        }
        return true;
    }
}
