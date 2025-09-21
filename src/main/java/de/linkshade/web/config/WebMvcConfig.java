package de.linkshade.web.config;

import de.linkshade.security.AuthenticationService;
import de.linkshade.services.RateLimitService;
import de.linkshade.web.interceptors.RateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitService rateLimitService;
    private final AuthenticationService authenticationService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(rateLimitService, authenticationService))
                .addPathPatterns("/short-urls");
    }
}
