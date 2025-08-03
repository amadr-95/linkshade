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

import java.time.LocalDateTime;
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
        Integer expirationInDays;
        LocalDateTime createdAt = LocalDateTime.now();

        if (currentUser == null)
            expirationInDays = appProperties.shortUrlProperties().defaultExpiryDays();
        else if (shortUrlForm.expirationInDays() == null) expirationInDays = null;
        else expirationInDays = shortUrlForm.expirationInDays();

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(shortUrlForm.originalUrl())
                .shortenedUrl(shortenUrl(shortUrlForm))
                .createdByUser(currentUser) //either null or real user
                .isPrivate(shortUrlForm.isPrivate() != null && shortUrlForm.isPrivate()) //false by default if the user is not logged in
                .numberOfClicks(0L)
                .createdAt(createdAt)
                .expiresAt(expirationInDays == null ?
                        null :
                        createdAt.plusDays(expirationInDays)) //either the default value or the custom value picked by the authenticated user
                .build();

        shortUrlRepository.save(shortUrl);
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
                log.info("ShortUrl '{}' created successfully in {} attempts", shortUrl, attempts);
                return shortUrl.toString();
            }
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

        // check whether is expired, reactivate and exit the method
        LocalDateTime now = LocalDateTime.now();
        if (shortUrlEditForm.isExpired()) {
            shortUrl.setExpiresAt(now.plusDays(appProperties.shortUrlProperties().defaultExpiryDays()));
            shortUrlRepository.save(shortUrl);
            return shortUrl.getShortenedUrl();
        }

        if (shortUrlEditForm.daysToExpire() != null && (shortUrlEditForm.daysToExpire() < ValidationConstants.MIN_URL_EXPIRATION_DAYS ||
                shortUrlEditForm.daysToExpire() > ValidationConstants.MAX_URL_EXPIRATION_DAYS)) {
            throw new UrlUpdateException(String.format("Expiration in days '%s' exceeds the limits", shortUrlEditForm.daysToExpire()));
        }

        shortUrl.setExpiresAt(shortUrlEditForm.daysToExpire() == null ?
                null :
                now.plusDays(shortUrlEditForm.daysToExpire()));

        if (shortUrlRepository.findByShortenedUrl(shortUrlEditForm.shortenedUrl()).isPresent()
                && !shortUrl.getShortenedUrl().equals(shortUrlEditForm.shortenedUrl())) {
            throw new UrlUpdateException(String.format("ShortUrl '%s' already exists", shortUrlEditForm.shortenedUrl()));
        }

        shortUrl.setShortenedUrl(shortUrlEditForm.isRandom() ?
                generateRandomShortUrl(null)
                : shortUrlEditForm.shortenedUrl());
        shortUrl.setPrivate(shortUrlEditForm.isPrivate());

        shortUrlRepository.save(shortUrl);
        return shortUrl.getShortenedUrl();
    }
}
