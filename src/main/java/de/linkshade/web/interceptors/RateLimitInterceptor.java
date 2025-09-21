package de.linkshade.web.interceptors;

import de.linkshade.security.AuthenticationService;
import de.linkshade.services.RateLimitService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import static de.linkshade.util.Constants.REMAINING_TOKENS_WARNING;

@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimitService rateLimitService;
    private final AuthenticationService authenticationService;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        // if user is logged in do not apply any restriction
        if (authenticationService.getUserInfo().isPresent()) return true;

        String clientIpAddress = rateLimitService.getClientIpAddress(request);
        boolean allowed = rateLimitService.allowRequest(clientIpAddress);

        if (!allowed) {
            request.setAttribute("rateLimitReached", true);
            request.setAttribute("rateLimitMessage",
                    "Maximum number of URL created has been reached. Please wait either 1h or log in to create more");
        } else {
            long remainingTokens = rateLimitService.getRemainingTokens(clientIpAddress);
            if (remainingTokens <= REMAINING_TOKENS_WARNING)
                request.setAttribute("rateLimitWarning", remainingTokens == 0 ?
                        "You ran out of URLs to be created!" :
                        String.format("You have %s URLs left to be created", remainingTokens));
        }
        return true;
    }
}
