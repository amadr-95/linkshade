package de.linkshade.services;

import de.linkshade.config.AppProperties;
import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.exceptions.UrlException;
import de.linkshade.exceptions.UrlExpiredException;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UrlPrivateException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.mapper.ShortUrlMapper;
import de.linkshade.util.ValidationConstants;
import de.linkshade.web.controllers.dto.ShortUrlForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

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
    public ShortUrlDTO createShortUrl(ShortUrlForm shortUrlForm) throws UrlException {
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
        return shortUrlMapper.toShortUrlDTO(shortUrl);
    }

    private String shortenUrl(ShortUrlForm shortUrlForm) throws UrlException {
        if (shortUrlForm.isCustom() != null && shortUrlForm.isCustom()) {
            // No need to verify if customURL already exists, it is handled in CustomUrlNameValidator
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
        for (int attemps = 1; attemps <= maxAttempts; attemps++) {
            for (int i = 0; i < urlLength; i++) {
                shortUrl.append(characters.charAt(random.nextInt(characters.length())));
            }
            if (!shortUrlRepository.existsByShortenedUrl(shortUrl.toString())) {
                log.info("ShortUrl '{}' created successfully in {} attempts", shortUrl, attemps);
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

        LocalDateTime expiresAt = shortUrl.getExpiresAt();
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now()))
            throw new UrlExpiredException(String.format("URL '%s' is expired", url));

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
        if (!shortUrl.getCreatedByUser().getId().equals(currentUser.getId())) {
            log.warn("Accessing private url '{}' with wrong user", shortUrl.getShortenedUrl());
            throw new UrlPrivateException("Trying to access to private URL with wrong user");
        }
    }
}
