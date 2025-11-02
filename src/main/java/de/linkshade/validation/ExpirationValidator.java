package de.linkshade.validation;

import de.linkshade.config.Constants;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ExpirationValidator {

    private final ValidationContextBuilder contextBuilder;

    public boolean isValid(ConstraintValidatorContext context, LocalDate expirationDate) {
        LocalDate now = LocalDate.now();
        if (expirationDate.isBefore(now) ||
                expirationDate.isAfter(now.plusDays(Constants.MAX_SHORTURL_EXPIRATION_DAYS))) {
            contextBuilder.buildContext(context, "{validation.urlForm.expirationDate}", "expirationDate");
            return false;
        }
        return true;
    }
}
