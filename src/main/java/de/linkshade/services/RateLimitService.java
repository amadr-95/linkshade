package de.linkshade.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.linkshade.config.AppProperties;
import de.linkshade.security.AuthenticationService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static de.linkshade.utils.IpAddressUtils.getClientIpAddress;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final AppProperties appProperties;
    private final AuthenticationService authenticationService;

    /**
     * Cache with automatic expiration to prevent memory leaks.
     * Buckets are evicted after 2 hours of inactivity.
     */
    private Cache<@NonNull String, Bucket> buckets;

    @PostConstruct
    public void postConstruct() {
        this.buckets = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(appProperties.securityProperties().bucketsExpirationTimeHours()))
                .build();
    }

    public long consumeToken(String bucketKey) {
        return getBucket(bucketKey).tryConsumeAndReturnRemaining(1).getRemainingTokens();
    }

    public long getRemainingTokens(String bucketKey) {
        return getBucket(bucketKey).getAvailableTokens();
    }

    /**
     * Retrieves or creates a rate limiting bucket for the specified IP address.
     * Uses concurrent map's computeIfAbsent for thread-safe lazy initialization.
     *
     * @param key the key generated based on whether the user is logged in or not and the client's IP address
     * @return the Bucket instance associated with the IP address
     */
    private Bucket getBucket(String key) {
        return buckets.get(key, this::createBucket);
    }

    private Bucket createBucket(String bucketKey) {
        int limit = bucketKey.endsWith("logged") ?
                appProperties.securityProperties().maxRequestLoggedUser() :
                appProperties.securityProperties().maxRequestAnonymousUser();

        Bandwidth bandwidth = Bandwidth.classic(limit,
                Refill.intervally(limit, Duration.ofHours(appProperties.securityProperties().rateLimitDurationHours())));

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    /**
     * Creates a bucket key from the HTTP request by extracting the client IP
     * and determining authentication status.
     */
    public String createBucketKeyFromRequest(HttpServletRequest request) {
        String clientIpAddress = getClientIpAddress(request);
        return createBucketKey(clientIpAddress);
    }

    private String createBucketKey(String ipAddress) {
        return String.format("%s#%s", ipAddress, (authenticationService.getUserInfo().isPresent() ?
                "logged" : "anonymous"));
    }

}
