package de.linkshade.security;

import de.linkshade.domain.entities.User;
import de.linkshade.security.oauth.OAuth2UserImpl;
import de.linkshade.util.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Optional<OAuth2UserImpl> getOAuth2User() {
        return Optional.ofNullable(getAuthentication())
                .filter(auth -> !auth.getPrincipal().equals(Constants.ANONYMOUS_USER_NAME))
                .map(auth -> (OAuth2UserImpl) auth.getPrincipal());
    }

    public Optional<User> getUserInfo() {
        return getOAuth2User().map(OAuth2UserImpl::user);
    }

    public String getUserName() {
        return getUserInfo()
                .map(User::getName)
                .orElse(Constants.DEFAULT_USER_NAME);
    }

    public Optional<Long> getUserId() {
        return getUserInfo()
                .map(User::getId);
    }

    public Optional<String> getAvatarUrl() {
        return getOAuth2User()
                .map(OAuth2UserImpl::getAvatarUrl);
    }
}
