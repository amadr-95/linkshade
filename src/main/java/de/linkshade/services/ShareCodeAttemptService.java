package de.linkshade.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.linkshade.config.AppProperties;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static de.linkshade.utils.IpAddressUtils.getClientIpAddress;


@Service
@RequiredArgsConstructor
public class ShareCodeAttemptService {

    private final AppProperties appProperties;

    private Cache<@NonNull String, Integer> attemptCache;

    @PostConstruct
    public void postConstruct() {
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(appProperties.securityProperties().codeTriesDurationMinutes()))
                .maximumSize(1_000)
                .build();
    }

    public boolean hasExceededMaxAttempts(String key) {
        Integer attempts = attemptCache.getIfPresent(key);
        return attempts != null && attempts >= appProperties.securityProperties().numberCodeTries();
    }

    public void recordFailedAttempt(String key) {
        Integer currentAttempts = attemptCache.getIfPresent(key);
        attemptCache.put(key, currentAttempts == null ? 1 : currentAttempts + 1);
    }

    public void resetAttempts(String key) {
        attemptCache.invalidate(key);
    }

    public String createKeyFromRequest(String shortUrl, HttpServletRequest request) {
        String clientIpAddress = getClientIpAddress(request);
        return createKey(clientIpAddress, shortUrl);
    }

    private String createKey(String ipAddress, String shortUrl ) {
        return String.format("%s#%s", ipAddress, shortUrl);
    }

}
