package de.linkshade.web.interceptors;

import de.linkshade.services.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;


@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        String clientIpAddress = rateLimitService.getClientIpAddress(request);
        long remainingTokens = rateLimitService.getRemainingTokens(clientIpAddress);

        if (remainingTokens == 0)
            request.setAttribute("rateLimitReached", true);

        return true;
    }
}
