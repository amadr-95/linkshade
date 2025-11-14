package de.linkshade.utils;

import jakarta.servlet.http.HttpServletRequest;

import static de.linkshade.config.Constants.X_FORWARDED_FOR;

public class IpAddressUtils {

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
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (xForwardedFor != null && !xForwardedFor.isBlank())
            return xForwardedFor.split(",")[0];

        return request.getRemoteAddr();
    }
}
