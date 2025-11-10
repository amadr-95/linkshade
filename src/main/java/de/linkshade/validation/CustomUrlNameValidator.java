package de.linkshade.validation;

import de.linkshade.config.Constants;
import de.linkshade.repositories.ShortUrlRepository;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomUrlNameValidator {

    private final ValidationContextBuilder contextBuilder;
    private final LengthValidator lengthValidator;
    private final ShortUrlRepository shortUrlRepository;

    public boolean isValid(ConstraintValidatorContext context, String shortenedUrl) {
        log.debug("Checking custom url name: '{}'", shortenedUrl);
        if (shortenedUrl == null || shortenedUrl.isBlank()) {
            log.error("ShortenedUrl '{}' is null or blank", shortenedUrl);
            contextBuilder.buildContext(context, "{validation.urlForm.invalidCustomUrlName.name}", Constants.CUSTOM_URL_NAME);
            return false;
        }

        int shortenerUrlLength = shortenedUrl.length();
        if (!lengthValidator.isValid(context, shortenerUrlLength, Constants.CUSTOM_URL_NAME)) {
            log.error("ShortenedUrl '{}' is outside limits", shortenedUrl);
            return false;
        }

        if (shortenedUrl.chars().anyMatch(c ->
                !Constants.VALID_CHARACTERS.contains(String.valueOf((char) c)))) {
            log.error("ShortenedUrl '{}' contains invalid characters", shortenedUrl);
            contextBuilder.buildContext(context, "{validation.urlForm.invalidCustomUrlName.characters}", Constants.CUSTOM_URL_NAME);
            return false;
        }

        if (shortUrlRepository.existsByShortenedUrl(shortenedUrl)) {
            log.error("Duplicate key: shortenedUrl '{}' already exists", shortenedUrl);
            contextBuilder.buildContext(context, "{validation.urlForm.invalidCustomUrlName.nameExists}", Constants.CUSTOM_URL_NAME);
            return false;
        }

        log.info("Custom url name: '{}' valid", shortenedUrl);
        return true;
    }
}

