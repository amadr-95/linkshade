package de.linkshade.security;

import de.linkshade.domain.entities.User;
import de.linkshade.security.oauth.OAuth2UserImpl;
import de.linkshade.util.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private OAuth2UserImpl getOAuth2User() {
        Authentication authentication = getAuthentication();
        if (authentication == null) return null;
        return authentication.getPrincipal().equals(Constants.ANONYMOUS_USER_NAME) ?
                null :
                ((OAuth2UserImpl) authentication.getPrincipal());
    }

    public User getUserInfo() {
        OAuth2UserImpl oAuth2User = getOAuth2User();
        return oAuth2User == null ? null : oAuth2User.user();
    }

    public String getUserName() {
        return getUserInfo() == null ? Constants.DEFAULT_USER_NAME : getUserInfo().getName();
    }

    public Long getUserId() {
        return getUserInfo() == null ? null : getUserInfo().getId();
    }

    public String getAvatarUrl() {
        return getOAuth2User() == null ? null : getOAuth2User().getAvatarUrl();
    }
}
