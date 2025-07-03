package de.linkshade.util;

import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LengthValidator {

    private final ValidationContextBuilder contextBuilder;

    public boolean isValid(ConstraintValidatorContext context, Integer length, String field) {
        if (length < ValidationConstants.MIN_URL_LENGTH ||
                length > ValidationConstants.MAX_URL_LENGTH) {
            contextBuilder.buildContext(context, "{validation.urlLength.range}", field);
            return false;
        }
        return true;
    }
}
