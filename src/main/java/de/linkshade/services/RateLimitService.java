package de.linkshade.services;

import de.linkshade.config.AppProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static de.linkshade.util.Constants.CONSECUTIVE_ERRORS_ALLOWED;
import static de.linkshade.util.Constants.X_FORWARDED_FOR;

@Service
@RequiredArgsConstructor
public class RateLimitService {

    private final AppProperties properties;
    private final Map<String, Integer> consecutiveErrorsCount = new ConcurrentHashMap<>();

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
            return xForwardedFor.split(",")[0];

        return request.getRemoteAddr();
    }

    public boolean allowRequest(String ipAddress) {
        return getBucket(ipAddress).tryConsume(1);
    }

    public long getRemainingTokens(String ipAddress) {
        return getBucket(ipAddress).getAvailableTokens();
    }

    public void addExtraToken(String ipAddress) {
        if (getConsecutiveErrors(ipAddress) < CONSECUTIVE_ERRORS_ALLOWED)
            getBucket(ipAddress).addTokens(1);
    }

    public void incrementConsecutiveErrors(String ipAddress) {
        consecutiveErrorsCount.compute(ipAddress, (key, count) -> count == null ? 1 : count + 1);
    }

    private int getConsecutiveErrors(String ipAddress) {
        return consecutiveErrorsCount.getOrDefault(ipAddress, 0);
    }

    private Bucket createBucket() {
        int limit = properties.maxRequestPerHour();

        Bandwidth bandwidth = Bandwidth.classic(limit, Refill.intervally(limit, Duration.ofHours(1)));

        return Bucket.builder()
                .addLimit(bandwidth)
                .build();
    }

    /**
     * Retrieves or creates a rate limiting bucket for the specified IP address.
     * Uses concurrent map's computeIfAbsent for thread-safe lazy initialization.
     *
     * @param ipAddress the client's IP address
     * @return the Bucket instance associated with the IP address
     */
    private Bucket getBucket(String ipAddress) {
        return buckets.computeIfAbsent(ipAddress, key -> createBucket());
    }
}
