package com.amador.urlshortener.security;

import com.amador.urlshortener.domain.entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    public User getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // authentication cannot be null in any case because even if the user is anonymous, Spring creates an
        // authenticated object with anonymousUser as principal. That's why isAuthenticated() method will always be true
        return authentication.getPrincipal().equals("anonymousUser") ?
                null :
                (User) authentication.getPrincipal();
    }

    @Deprecated
    public User getCurrentUserInfoAlternative() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
        }
        return null;
    }
}
