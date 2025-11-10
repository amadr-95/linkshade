package de.linkshade.validation;

import de.linkshade.config.Constants;
import de.linkshade.security.AuthenticationService;
import de.linkshade.validation.annotations.ValidUrlForm;
import de.linkshade.web.controllers.dto.ShortUrlForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class UrlFormValidator implements ConstraintValidator<ValidUrlForm, ShortUrlForm> {

    private final UrlValidator urlValidator;
    private final ExpirationValidator expirationDateValidator;
    private final LengthValidator lengthValidator;
    private final CustomUrlNameValidator customUrlNameValidator;
    private final AuthenticationService authenticationService;

    @Override
    public boolean isValid(ShortUrlForm shortUrlForm, ConstraintValidatorContext context) {
        //disable default error message because specific ones will be provided
        context.disableDefaultConstraintViolation();
        // When user is not logged in
        if (!lengthValidator.isValid(context, shortUrlForm.originalUrl().length(), Constants.ORIGINAL_URL))
            return false;
        if (!urlValidator.isValid(context, shortUrlForm.originalUrl())) {
            return false;
        }
        // User is logged in
        if (authenticationService.getUserInfo().isPresent()) {
            if (shortUrlForm.expirationDate() != null && !expirationDateValidator.isValid(context, shortUrlForm.expirationDate())) {
                return false;
            }
            if (shortUrlForm.isCustom()) {
                return customUrlNameValidator.isValid(context, shortUrlForm.customShortUrlName());
            } else {
                return lengthValidator.isValid(context, shortUrlForm.urlLength(), Constants.URL_LENGTH);
            }
        }
        return true;
    }
}


