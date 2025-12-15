package de.linkshade.validation;

import de.linkshade.config.AppProperties;
import de.linkshade.config.Constants;
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
    private final AppProperties appProperties;

    public boolean isValid(ConstraintValidatorContext context, Integer length, String field) {

        if (field.equals(Constants.ORIGINAL_URL)) {
            if (length > appProperties.shortUrlProperties().maxOriginalUrlLength()) {
                contextBuilder.buildContext(context, "{validation.urlForm.originalUrlLength.range}", field);
                return false;
            }
        }

        boolean lengthOutsideLimits = length < appProperties.shortUrlProperties().minShorturlLength() ||
                length > appProperties.shortUrlProperties().maxShorturlLength();

        if (field.equals(Constants.URL_LENGTH)) {
            if (lengthOutsideLimits) {
                log.warn("User {} is introducing wrong values intentionally. Length value: {}. Range expected: {} - {}",
                        authenticationService.getUserInfo().orElse(null), length,
                        appProperties.shortUrlProperties().minShorturlLength(),
                        appProperties.shortUrlProperties().maxShorturlLength());
                //this message will not be print in the frontend, but it needs to be provided
                contextBuilder.buildContext(context, "{validation.urlForm.shortUrlLength.range}", field);
                return false;
            }
        }

        if (field.equals(Constants.CUSTOM_URL_NAME)) {
            if (lengthOutsideLimits) {
                contextBuilder.buildContext(context, "{validation.urlForm.invalidCustomUrlName.length}", field);
                return false;
            }
        }

        return true;
    }
}
