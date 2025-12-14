package de.linkshade.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.linkshade.config.Constants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static de.linkshade.utils.IpAddressUtils.getClientIpAddress;


@Service
public class ShareCodeAttemptService {

    private final Cache<@NonNull String, Integer> attemptCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(Constants.CODE_TRIES_DURATION_IN_MINUTES))
            .maximumSize(1_000)
            .build();

    public boolean hasExceededMaxAttempts(String key) {
        Integer attempts = attemptCache.getIfPresent(key);
        return attempts != null && attempts >= Constants.NUMBER_OF_CODE_TRIES;
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
