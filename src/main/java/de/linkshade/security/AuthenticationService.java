package de.linkshade.security;

import de.linkshade.security.oauth.OAuth2UserImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public OAuth2UserImpl getCurrentUserInfo() {
        Authentication authentication = getAuthentication();
        if (authentication == null) return null;
        return authentication.getPrincipal().equals("anonymousUser") ?
                null :
                ((OAuth2UserImpl) authentication.getPrincipal());
    }

    public String getUserName() {
        OAuth2UserImpl currentUserInfo = getCurrentUserInfo();
        return currentUserInfo == null ? "Guest" : currentUserInfo.user().getName();
    }

    public Long getUserId() {
        OAuth2UserImpl currentUserInfo = getCurrentUserInfo();
        return currentUserInfo == null ? null : currentUserInfo.user().getId();
    }

    public String getAvatarUrl() {
        OAuth2UserImpl currentUserInfo = getCurrentUserInfo();
        return currentUserInfo == null ? null : currentUserInfo.getAvatarUrl();
    }
}
