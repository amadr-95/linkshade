package de.linkshade.services;

import de.linkshade.config.AppProperties;
import de.linkshade.security.AuthenticationService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.linkshade.util.Constants.X_FORWARDED_FOR;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final AppProperties properties;
    private final AuthenticationService authenticationService;

    /**
     * Thread-safe map to store rate-limiting buckets by IP address.
     * ConcurrentHashMap is used instead of HashMap to ensure thread safety in a multithreaded environment,
     * supporting atomic operations like computeIfAbsent and concurrent read access without locking,
     * which is essential for a high-performance rate limiting service.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Retrieves the actual client IP address from an HTTP request.
     * This method handles requests that may pass through proxies or load balancers
     * by first checking the X-Forwarded-For header, which contains the original client IP
     * as its first entry in a comma-separated list.
     * If this header isn't available, falls back to the direct connection IP.
     *
     * @param request The HTTP request from which to extract the client IP address
     * @return The client's original IP address as a string
     */
    public String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (xForwardedFor != null && !xForwardedFor.isBlank())
            return enrichIpAddress(xForwardedFor.split(",")[0]);

        return enrichIpAddress(request.getRemoteAddr());
    }

    public long consumeToken(String ipAddress) {
        return getBucket(ipAddress).tryConsumeAndReturnRemaining(1).getRemainingTokens();
    }

    public long getRemainingTokens(String ipAddress) {
        return getBucket(ipAddress).getAvailableTokens();
    }

    private Bucket createBucket() {
        int limit = authenticationService.getUserInfo().isPresent() ?
                properties.maxRequestLoggedUser() :
                properties.maxRequestAnonymousUser();

        Bandwidth bandwidth = Bandwidth.classic(limit, Refill.intervally(limit, Duration.ofHours(1)));

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    /**
     * Retrieves or creates a rate limiting bucket for the specified IP address.
     * Uses concurrent map's computeIfAbsent for thread-safe lazy initialization.
     *
     * @param key the key generated based on whether the user is logged in or not and the client's IP address
     * @return the Bucket instance associated with the IP address
     */
    private Bucket getBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createBucket());
    }

    private String enrichIpAddress(String ipAddress) {
        boolean isAuthenticated = authenticationService.getUserInfo().isPresent();
        return ipAddress + ":" + (isAuthenticated ? "logged" : "anonymous");
    }
}
