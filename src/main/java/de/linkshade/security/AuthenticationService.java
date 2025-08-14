package de.linkshade.security;

import de.linkshade.domain.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public User getCurrentUserInfo() {
        Authentication authentication = getAuthentication();
        if (authentication == null) return null;
        return authentication.getPrincipal().equals("anonymousUser") ?
                null :
                (User) authentication.getPrincipal();
    }

    public String getUserName() {
        User user = getCurrentUserInfo();
        return user == null ? "Guest" : user.getName();
    }

    public Long getUserId() {
        User user = getCurrentUserInfo();
        return user == null ? null : user.getId();
    }
}
