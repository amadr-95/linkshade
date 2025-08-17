package de.linkshade.services;

import de.linkshade.config.AppProperties;
import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.Role;
import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.exceptions.*;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.mapper.ShortUrlMapper;
import de.linkshade.util.ValidationConstants;
import de.linkshade.web.controllers.dto.ShortUrlEditForm;
import de.linkshade.web.controllers.dto.ShortUrlForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlMapper shortUrlMapper;
    private final AuthenticationService authenticationService;
    private final AppProperties appProperties;
    private final PaginationService paginationService;

    public PagedResult<ShortUrlDTO> findAllPublicUrls(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, shortUrlRepository::countAllPublicUrls);

        // Wrapper object that allows us to store all the data coming from Page so we can show it in the frontend
        return PagedResult.from(shortUrlRepository.findAllPublicUrls(validPage)
                .map(shortUrlMapper::toShortUrlDTO));
    }

    @Transactional
    public String createShortUrl(ShortUrlForm shortUrlForm) throws UrlException {
        User currentUser = authenticationService.getCurrentUserInfo();
        LocalDate expirationDate;
        LocalDate createdAt = LocalDate.now();

        if (currentUser == null)
            expirationDate = createdAt.plusDays(appProperties.shortUrlProperties().defaultExpiryDays());
        else if (shortUrlForm.expirationDate() == null) expirationDate = null;
        else expirationDate = shortUrlForm.expirationDate();

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(shortUrlForm.originalUrl())
                .shortenedUrl(shortenUrl(shortUrlForm))
                .createdByUser(currentUser) //either null or real user
                .isPrivate(shortUrlForm.isPrivate() != null && shortUrlForm.isPrivate()) //false by default if the user is not logged in
                .numberOfClicks(0L)
                .createdAt(createdAt)
                .expiresAt(expirationDate) //either the default value or the custom value picked by the authenticated user
                .build();

        shortUrlRepository.save(shortUrl);
        log.info("ShortUrl '{}' successfully stored in DB", shortUrl.getShortenedUrl());
        return shortUrl.getShortenedUrl();
    }

    private String shortenUrl(ShortUrlForm shortUrlForm) throws UrlException {
        if (shortUrlForm.isCustom() != null && shortUrlForm.isCustom()) {
            // No need to verify if customURL already exists, it is handled in CustomUrlNameValidator
            // (so the errorMessage can be shown under the field)
            return shortUrlForm.customShortUrlName();
        }
        return generateRandomShortUrl(shortUrlForm.urlLength());
    }

    private String generateRandomShortUrl(Integer urlLength) throws UrlException {
        Random random = new Random();
        String characters = ValidationConstants.VALID_CHARACTERS;
        urlLength = urlLength == null ?
                appProperties.shortUrlProperties().defaultUrlLength() : urlLength;
        StringBuilder shortUrl = new StringBuilder(urlLength);
        int maxAttempts = appProperties.numberOfTries();
        for (int attempts = 1; attempts <= maxAttempts; attempts++) {
            for (int i = 0; i < urlLength; i++) {
                shortUrl.append(characters.charAt(random.nextInt(characters.length())));
            }
            if (!shortUrlRepository.existsByShortenedUrl(shortUrl.toString())) {
                log.info("ShortUrl '{}' successfully generated in {} attempts", shortUrl, attempts);
                return shortUrl.toString();
            }
            //clean stringBuilder for subsequent tries
//            shortUrl = new StringBuilder();
        }
        throw new UrlException("ShortenedUrl could not be created: too many attempts");
    }

    @Transactional
    public String accessOriginalUrl(String url) throws UrlException {
        if (url == null || url.isBlank()) throw new UrlException("URL is null or empty");
        ShortUrl shortUrl = shortUrlRepository.findByShortenedUrl(url)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL '%s' not found", url)));

        if (shortUrl.isExpired()) throw new UrlExpiredException(String.format("URL '%s' is expired", url));

        validateUserPermissions(authenticationService.getCurrentUserInfo(), shortUrl);

        shortUrl.setNumberOfClicks(shortUrl.getNumberOfClicks() + 1);
        shortUrlRepository.save(shortUrl);
        return shortUrl.getOriginalUrl();
    }

    private void validateUserPermissions(User currentUser, ShortUrl shortUrl) throws UrlPrivateException {
        if (!shortUrl.isPrivate()) {
            log.info("Accessing public url '{}'", shortUrl.getShortenedUrl());
            return; //public urls are accessible by all
        }
        if (currentUser == null) {
            log.warn("Accessing private url '{}' without logging in", shortUrl.getShortenedUrl());
            throw new UrlPrivateException("Trying to access to private URL without logging in");
        }
        if (shortUrl.getCreatedByUser() != null && !shortUrl.getCreatedByUser().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(Role.ADMIN)) {
            log.warn("Accessing private url '{}' with wrong user: '{}', expected: '{}'", shortUrl.getShortenedUrl(),
                    currentUser.getEmail(), shortUrl.getCreatedByUser().getEmail());
            throw new UrlPrivateException("Trying to access to private URL with wrong user");
        }
    }

    @Transactional
    public String updateUrl(UUID urlId, ShortUrlEditForm shortUrlEditForm) throws UrlException {
        if (authenticationService.getCurrentUserInfo() == null)
            throw new UrlUpdateException("Trying to edit an URL without logging in");

        ShortUrl shortUrl = shortUrlRepository.findById(urlId)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL '%s' not found", urlId)));

        User currentUserInfo = authenticationService.getCurrentUserInfo();
        if (!currentUserInfo.getRole().toString().equals("ADMIN") &&
                !currentUserInfo.getId().equals(shortUrl.getCreatedByUser().getId()))
            throw new UrlUpdateException(String.format("Trying to remove an URL with wrong user. Expected userId: '%s', got: '%s'",
                    shortUrl.getCreatedByUser().getId(), currentUserInfo.getId()));

        // check whether is expired, if so reactivate and exit the method
        LocalDate now = LocalDate.now();
        if (shortUrlEditForm.isExpired()) {
            shortUrl.setExpiresAt(now.plusDays(appProperties.shortUrlProperties().defaultExpiryDays()));
            shortUrlRepository.save(shortUrl);
            return shortUrl.getShortenedUrl();
        }

        // check for real changes
        LocalDate expirationDateForm = shortUrlEditForm.expirationDate();
        boolean isPrivateForm = shortUrlEditForm.isPrivate();
        String shortenedUrlForm = shortUrlEditForm.shortenedUrl();
        boolean expirationChanged = !Objects.equals(expirationDateForm, shortUrl.getExpiresAt());
        boolean privacyChanged = isPrivateForm != shortUrl.isPrivate();
        boolean shortenedUrlChanged = !Objects.equals(shortenedUrlForm, shortUrl.getShortenedUrl());

        if (!expirationChanged && !privacyChanged && !shortenedUrlChanged)
            return shortUrl.getShortenedUrl();

        // check individual changes and update the values
        if (expirationChanged) {
            // Validate expirationDate
            if (expirationDateForm != null &&
                    (expirationDateForm.isBefore(now) || expirationDateForm.isAfter(
                                    now.plusDays(ValidationConstants.MAX_URL_EXPIRATION_DAYS)))) {
                throw new UrlUpdateException(
                        String.format("Expiration date '%s' is before today or exceeds the limits", expirationDateForm));
            }
            shortUrl.setExpiresAt(expirationDateForm);
        }
        if (privacyChanged)
            shortUrl.setPrivate(isPrivateForm);
        if (shortenedUrlChanged) {
            if (shortUrlEditForm.isRandom())
                shortUrl.setShortenedUrl(generateRandomShortUrl(null));
            else {
                // Validate shortened value if not random
                if (shortenedUrlForm == null || shortenedUrlForm.isBlank())
                    throw new UrlUpdateException("Shortened value cannot be blank");
                if (shortenedUrlForm.length() < ValidationConstants.MIN_URL_LENGTH ||
                        shortenedUrlForm.length() > ValidationConstants.MAX_URL_LENGTH)
                    throw new UrlUpdateException(String.format("Shortened length '%s' exceeds the limits",
                            shortenedUrlForm.length()));
                // check that it's not existing already
                if (shortUrlRepository.findByShortenedUrl(shortenedUrlForm).isPresent()) {
                    throw new UrlUpdateException(String.format
                            ("ShortUrl '%s' already exists", shortenedUrlForm));
                }
                shortUrl.setShortenedUrl(shortenedUrlForm);
            }
        }
        shortUrlRepository.save(shortUrl);
        return shortUrl.getShortenedUrl();
    }
}
