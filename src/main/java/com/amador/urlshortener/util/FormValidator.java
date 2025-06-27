package com.amador.urlshortener.util;

import com.amador.urlshortener.security.AuthenticationService;
import com.amador.urlshortener.util.annotations.ValidForm;
import com.amador.urlshortener.web.controllers.dto.ShortUrlForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class FormValidator implements ConstraintValidator<ValidForm, ShortUrlForm> {

    private final UrlValidator urlValidator;
    private final ExpirationValidator expirationDaysValidator;
    private final LengthValidator lengthValidator;
    private final CustomUrlNameValidator customUrlNameValidator;
    private final AuthenticationService authenticationService;

    @Override
    public boolean isValid(ShortUrlForm shortUrlForm, ConstraintValidatorContext context) {
        //disable default error message because specific ones will be provided
        context.disableDefaultConstraintViolation();
        if (!urlValidator.isValid(context, shortUrlForm.originalUrl())) {
            return false;
        }
        if (authenticationService.getCurrentUserInfo() != null) {
            if (shortUrlForm.expirationInDays() != null && !expirationDaysValidator.isValid(context, shortUrlForm.expirationInDays())) {
                return false;
            }
            if (shortUrlForm.isCustom()) {
                return customUrlNameValidator.isValid(context, shortUrlForm.customShortUrlName());
            } else {
                return lengthValidator.isValid(context, shortUrlForm.urlLength(), "urlLength");
            }
        }
        return true;
    }
}


