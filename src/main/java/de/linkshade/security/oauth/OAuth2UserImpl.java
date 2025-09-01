package de.linkshade.security.oauth;

import de.linkshade.domain.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record OAuth2UserImpl(User user, Map<String, Object> attributes) implements OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getName() {
        return user.getName();
    }

    public String getAvatarUrl() {
        return attributes.get("avatarUrl").toString();
    }
}
