package de.linkshade.services;

import de.linkshade.config.AppProperties;
import de.linkshade.config.Constants;
import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.exceptions.UrlException;
import de.linkshade.exceptions.UrlExpiredException;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UrlPrivateException;
import de.linkshade.exceptions.UrlUpdateException;
import de.linkshade.exceptions.UserException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.mapper.ShortUrlMapper;
import de.linkshade.web.controllers.dto.ShortUrlEditForm;
import de.linkshade.web.controllers.dto.ShortUrlForm;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static de.linkshade.domain.entities.Role.ADMIN;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlMapper shortUrlMapper;
    private final AuthenticationService authenticationService;
    private final AppProperties appProperties;
    private final PaginationService paginationService;
    private Random random;

    @PostConstruct
    public void postConstruct() {
        this.random = new Random();
    }

    public PagedResult<ShortUrlDTO> findAllPublicUrls(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, shortUrlRepository::countAllPublicUrls);

        // Wrapper object that allows to store all the data coming from Page so it can be shown in the HTML
        return PagedResult.from(shortUrlRepository.findAllPublicUrls(validPage)
                .map(shortUrlMapper::toShortUrlDTO));
    }

    public PagedResult<ShortUrlDTO> findAllShortUrls(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, shortUrlRepository::countAll);

        return PagedResult.from(shortUrlRepository.findAllUrls(validPage)
                .map(shortUrlMapper::toShortUrlDTO));
    }

    public PagedResult<ShortUrlDTO> listUserUrls(Pageable pageableRequest) throws UserException {
        UUID userId = getUser().getId();
        Pageable validPage = paginationService.createValidPage(pageableRequest,
                () -> shortUrlRepository.countByCreatedByUser(userId));
        Page<ShortUrlDTO> shortUrlDTOS =
                shortUrlRepository.findAllByCreatedByUser(validPage, userId)
                        .map(shortUrlMapper::toShortUrlDTO);
        return PagedResult.from(shortUrlDTOS);
    }

    @Transactional
    public String createShortUrl(ShortUrlForm shortUrlForm) throws UrlException {
        Optional<User> currentUser = authenticationService.getUserInfo();
        ZoneId userTimezone = getZoneId(shortUrlForm.userTimezone());
        LocalDate expiresAt;

        // expiration depends on user's time zone
        if (currentUser.isEmpty()) {
            expiresAt = LocalDate.now(userTimezone)
                    .plusDays(appProperties.shortUrlProperties().defaultExpiryDays());
        } else if (shortUrlForm.expirationDate() == null) {
            expiresAt = null;
        } else {
            expiresAt = shortUrlForm.expirationDate();
        }

        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(shortUrlForm.originalUrl())
                .shortenedUrl(shortenUrl(shortUrlForm))
                .createdByUser(currentUser.orElse(null))
                .isPrivate(shortUrlForm.isPrivate() != null && shortUrlForm.isPrivate())
                .shareCode(null)
                .numberOfClicks(0L)
                .expiresAt(expiresAt)
                .zoneId(userTimezone.toString())
                .build();

        shortUrlRepository.save(shortUrl);
        log.debug("ShortUrl '{}' successfully stored in DB", shortUrl.getShortenedUrl());
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
        String characters = Constants.VALID_CHARACTERS;
        urlLength = urlLength == null ?
                appProperties.shortUrlProperties().defaultShortUrlLength() : urlLength;
        StringBuilder shortUrl = new StringBuilder(urlLength);
        int maxAttempts = appProperties.numberOfTries();
        for (int attempts = 1; attempts <= maxAttempts; attempts++) {
            shortUrl.setLength(0);
            for (int i = 0; i < urlLength; i++) {
                shortUrl.append(characters.charAt(random.nextInt(characters.length())));
            }
            if (!shortUrlRepository.existsByShortenedUrl(shortUrl.toString())) {
                return shortUrl.toString();
            }
        }
        throw new UrlException("ShortenedUrl could not be created: max attempts reached");
    }

    @Transactional
    public String accessOriginalUrl(String shortenedUrl) throws UrlException {
        ShortUrl shortUrl = shortUrlRepository.findByShortenedUrl(shortenedUrl)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL '%s' not found", shortenedUrl)));

        if (shortUrl.isExpired()) throw new UrlExpiredException(String.format("URL '%s' is expired", shortenedUrl));

        if (!shortUrl.isPrivate()) {
            incrementClicksAndSave(shortUrl);
            return shortUrl.getOriginalUrl();
        }

        if (userHasGrantedAccess(shortUrl)) {
            incrementClicksAndSave(shortUrl);
            return shortUrl.getOriginalUrl();
        }

        // private and shared
        if (shortUrl.getShareCode() != null) {
            // trigger for showing the form
            throw new UrlPrivateException(Constants.SHARE_CODE_REQUIRED);
        }

        // private and NOT shared
        throw new UrlPrivateException("Trying to access to private URL without proper permissions");
    }

    private boolean userHasGrantedAccess(ShortUrl shortUrl) {
        Optional<User> currentUser = authenticationService.getUserInfo();
        if (currentUser.isEmpty()) return false;

        User user = currentUser.get();
        boolean isOwner = shortUrl.getCreatedByUser() != null &&
                shortUrl.getCreatedByUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == ADMIN;

        return isOwner || isAdmin;
    }

    @Transactional
    public String verifySharingCode(String shortenedUrl, String code) throws UrlException {
        ShortUrl shortUrl = shortUrlRepository.findByShortenedUrl(shortenedUrl)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL '%s' not found", shortenedUrl)));

        if (!shortUrl.getShareCode().equals(code)) {
            throw new UrlPrivateException(String.format(
                    "Invalid sharing code for shortUrl '%s'. Expected '%s', got: '%s'",
                    shortenedUrl, shortUrl.getShareCode(), code));
        }
        incrementClicksAndSave(shortUrl);
        return shortUrl.getOriginalUrl();
    }

    private void incrementClicksAndSave(ShortUrl shortUrl) {
        shortUrl.setNumberOfClicks(shortUrl.getNumberOfClicks() + 1);
        shortUrlRepository.save(shortUrl);
    }

    @Transactional
    public String updateShortUrl(UUID urlId, ShortUrlEditForm shortUrlEditForm) throws UrlException {
        User user = getUser();
        ShortUrl shortUrl = shortUrlRepository.findById(urlId)
                .orElseThrow(() -> new UrlNotFoundException(String.format("URL '%s' not found", urlId)));

        if (user.getRole() != ADMIN &&
                !user.getId().equals(shortUrl.getCreatedByUser().getId()))
            throw new UrlUpdateException(String.format(
                    "Trying to edit an URL with wrong user. Expected userId: '%s', got: '%s'",
                    shortUrl.getCreatedByUser().getId(), user.getId()));

        // if it is expired, reactivate and exit the method
        ZoneId zoneId = getZoneId(shortUrl.getZoneId());
        LocalDate now = LocalDate.now(zoneId);
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
            return String.format("%s (No changes were made)", shortUrl.getShortenedUrl());

        // check individual changes and update the values if needed
        if (expirationChanged) {
            // Validate expirationDate
            if (expirationDateForm != null &&
                    (expirationDateForm.isBefore(now) || expirationDateForm.isAfter(
                            now.plusDays(appProperties.shortUrlProperties().maxShortUrlExpirationDays())))) {
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
                if (shortenedUrlForm.length() < appProperties.shortUrlProperties().minShorturlLength() ||
                        shortenedUrlForm.length() > appProperties.shortUrlProperties().maxShorturlLength())
                    throw new UrlUpdateException(String.format("Shortened length '%s' is outside the limits",
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

    @Transactional
    public int deleteSelectedUrls(List<UUID> shortUrlsIds) throws UrlNotFoundException {
        User user = getUser();
        if (shortUrlsIds.stream().anyMatch(Objects::isNull))
            throw new UrlNotFoundException("One or more URLs were null");
        return user.getRole() == ADMIN ?
                shortUrlRepository.deleteByIdIn(shortUrlsIds) :
                shortUrlRepository.deleteByIdInAndCreatedByUserId(shortUrlsIds, user.getId());
    }

    @Transactional
    public int reactivateExpiredUrls(String userTimezone) throws UserException {
        UUID userId = getUser().getId();

        List<UUID> expiredUrls = shortUrlRepository.findExpiredUrlIdsByUserId(userId);

        if (expiredUrls.isEmpty()) return 0;

        // assumes all URLs were created in the same time zone
        ZoneId zoneId = getZoneId(userTimezone);

        LocalDate newExpirationDate = LocalDate.now(zoneId)
                .plusDays(appProperties.shortUrlProperties().defaultExpiryDays());

        return shortUrlRepository.updateExpirationDateByUrlIds(expiredUrls, newExpirationDate);
    }

    @Transactional
    public int deleteExpiredUrls() throws UserException {
        UUID userId = getUser().getId();

        List<UUID> expiredUrls = shortUrlRepository.findExpiredUrlIdsByUserId(userId);

        if (expiredUrls.isEmpty()) return 0;

        return shortUrlRepository.deleteByIdInAndCreatedByUserId(expiredUrls, userId);
    }

    public int getExpiredUrlsCountByUserId(UUID userId) {
        return shortUrlRepository.numberOfExpiredUrlsByUserId(userId);
    }

    public int getAllNonCreatedByUserExpiredUrls() {
        return shortUrlRepository.numberOfAllNonCreatedByUserExpiredUrls();
    }

    @Transactional
    public SharingResult manageSharingPrivateUrl(UUID urlId) throws UrlException {
        User user = getUser();
        ShortUrl shortUrl = shortUrlRepository.findById(urlId).orElseThrow(
                () -> new UrlNotFoundException(String.format("URL '%s' not found", urlId))
        );

        if (user.getRole() != ADMIN &&
                !user.getId().equals(shortUrl.getCreatedByUser().getId()))
            throw new UrlPrivateException(String.format("Trying to change permissions to a private URL with wrong user. " +
                            "Expected userId: '%s', got: '%s'",
                    shortUrl.getCreatedByUser().getId(), user.getId()));

        // deactivating sharing state
        if (shortUrl.getShareCode() != null) {
            shortUrl.setShareCode(null);
            shortUrlRepository.save(shortUrl);
            return new SharingResult(shortUrl.getShortenedUrl(), null);
        }

        //activating sharing state
        shortUrl.setShareCode(generateSharingCode());
        shortUrlRepository.save(shortUrl);
        return new SharingResult(shortUrl.getShortenedUrl(), shortUrl.getShareCode());
    }

    // this code can be the same multiple times, since the unique combination is shortUrl (unique) + sharingCode
    private String generateSharingCode() {
        int sharingCodeLength = appProperties.shortUrlProperties().sharingCodeLength();
        String characters = Constants.VALID_CHARACTERS;
        StringBuilder code = new StringBuilder(sharingCodeLength);
        for (int i = 0; i < sharingCodeLength; i++) {
            code.append(characters.charAt(random.nextInt(characters.length())));
        }
        return code.toString();
    }

    private User getUser() throws UserException {
        return authenticationService.getUserInfo()
                .orElseThrow(() -> new UserException("User not authenticated"));
    }

    private ZoneId getZoneId(String userTimezone) {
        try {
            return ZoneId.of(userTimezone);
        } catch (DateTimeException ex) {
            log.warn("Invalid timezone provided: '{}'. Using UTC as fallback", userTimezone);
            return ZoneId.of("UTC");
        }
    }
}