package com.amador.urlshortener.util;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpirationValidator {

    private final ValidationContextBuilder contextBuilder;

    public boolean isValid(ConstraintValidatorContext context, Integer expirationInDays) {
        if (expirationInDays < ValidationConstants.MIN_URL_EXPIRATION_DAYS ||
                expirationInDays > ValidationConstants.MAX_URL_EXPIRATION_DAYS) {
            contextBuilder.buildContext(context, "{validation.defaultExpiryDays.range}", "expirationInDays");
            return false;
        }
        return true;
    }
}
