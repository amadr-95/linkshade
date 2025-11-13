package de.linkshade.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.linkshade.config.Constants;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;


@Service
public class ShareCodeAttemptService {

    private final Cache<@NonNull String, Integer> attemptCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .maximumSize(1_000)
            .build();

    public boolean hasExceededMaxAttempts(String shortUrl) {
        Integer attempts = attemptCache.getIfPresent(shortUrl);
        return attempts != null && attempts >= Constants.NUMBER_OF_SHARING_CODE_TRIES;
    }

    public void recordFailedAttempt(String shortUrl) {
        Integer currentAttempts = attemptCache.getIfPresent(shortUrl);
        attemptCache.put(shortUrl, currentAttempts == null ? 1 : currentAttempts + 1);
    }

    public void resetAttempts(String shortUrl) {
        attemptCache.invalidate(shortUrl);
    }

}
