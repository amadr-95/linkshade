package de.linkshade.security.oauth;

import de.linkshade.domain.entities.AuthProvider;
import de.linkshade.domain.entities.User;
import de.linkshade.repositories.UserRepository;
import de.linkshade.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        AuthProvider oAuthProviderName = AuthProvider.from(userRequest.getClientRegistration().getRegistrationId());
        Map<String, Object> attributes = extractAttributes(oAuthProviderName, oAuth2User);

        //caution: in GitHub might be null if the user did not select any by default on their account
        String email = attributes.get("email") == null ? null : attributes.get("email").toString();
        String userProviderId = attributes.get("userProviderId").toString();

        User user = findOrCreatedUser(userProviderId, email, attributes, oAuthProviderName);
        return new OAuth2UserImpl(user, attributes);
    }

    private User findOrCreatedUser(String userProviderId, String email, Map<String, Object> attributes, AuthProvider oAuthProviderName) {
        return userRepository.findByUserProviderId(userProviderId)
                .orElseGet(() -> {
                    // It might exist by userProviderId, but it also worth searching by email,
                    // in case the user has accounts from different providers but the same
                    // email in all of them
                    if (email != null) {
                        return userRepository.findUserByEmail(email)
                                .orElseGet(() -> {
                                    User registeredUser = userService.registerUser(attributes, oAuthProviderName);
                                    log.info("User not found in database, registered from provider {}, user={}", registeredUser.getAuthProvider(), registeredUser);
                                    return registeredUser;
                                });
                    }
                    User registeredUser = userService.registerUser(attributes, oAuthProviderName);
                    log.info("User not found in database (email is null), registered from provider {}, user={}", registeredUser.getAuthProvider(), registeredUser);
                    return registeredUser;
                });
    }

    private Map<String, Object> extractAttributes(AuthProvider oAuthProvider, OAuth2User oAuth2User) {
        Map<String, Object> attributes = new HashMap<>();
        // common provider attributes
        attributes.put("email", oAuth2User.getAttribute("email"));

        switch (oAuthProvider) {
            case GITHUB -> {
                attributes.put("name", oAuth2User.getAttribute("name"));
                attributes.put("userProviderId", oAuth2User.getAttribute("id"));
                // GitHub always sends avatar_url as a string (url). In case the user does not have an image,
                // GitHub generates an icon by default that can be rendered in img html tag
                attributes.put("avatarUrl", oAuth2User.getAttribute("avatar_url"));
            }
            case GOOGLE -> {
                attributes.put("name", oAuth2User.getAttribute("given_name"));
                attributes.put("userProviderId", oAuth2User.getAttribute("sub"));
                // UserInfo endpoint includes picture attribute
                // Google generates a default image if the user did not upload any, so it can also be rendered under tha img html tag
                attributes.put("avatarUrl", oAuth2User.getAttribute("picture"));
            }
            default -> attributes.put("userProviderId", "Not supported");
        }
        return attributes;
    }
}
