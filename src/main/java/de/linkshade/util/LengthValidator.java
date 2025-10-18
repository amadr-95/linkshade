package de.linkshade.util;

import de.linkshade.security.AuthenticationService;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LengthValidator {

    private final ValidationContextBuilder contextBuilder;
    private final AuthenticationService authenticationService;

    public boolean isValid(ConstraintValidatorContext context, Integer length, String field) {

        if (field.equals("urlLength")) {
            if (length < Constants.MIN_SHORTURL_LENGTH || length > Constants.MAX_SHORTURL_LENGTH) {
                log.warn("User {} is introducing wrong values intentionally. Length value: {}. Range expected: {} - {}",
                        authenticationService.getUserInfo().orElse(null), length,
                        Constants.MIN_SHORTURL_LENGTH, Constants.MAX_SHORTURL_LENGTH);
                //this message will not be print in the frontend, but it needs to be provided
                contextBuilder.buildContext(context, "{validation.urlForm.shortUrlLength.range}", field);
                return false;
            }
        } else if (field.equals("originalUrl")) {
            if (length > Constants.MAX_ORIGINAL_URL_LENGTH) {
                contextBuilder.buildContext(context, "{validation.urlForm.originalUrlLength.range}", field);
                return false;
            }
        }
        return true;
    }
}
