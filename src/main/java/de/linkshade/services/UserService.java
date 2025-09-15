package de.linkshade.services;

import de.linkshade.domain.entities.AuthProvider;
import de.linkshade.domain.entities.Role;
import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.User;
import de.linkshade.exceptions.UserException;
import de.linkshade.exceptions.UserNotFoundException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
import de.linkshade.security.AuthenticationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;

    @Transactional
    public User registerUser(Map<String, Object> userAttributes, AuthProvider oAuthProvider) {

        User user = User.builder()
                .name(userAttributes.get("name").toString())
                .email(userAttributes.get("email") == null ? "Not provided" : userAttributes.get("email").toString())
                .userProviderId(userAttributes.get("userProviderId").toString())
                .authProvider(oAuthProvider)
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public int deleteUser(Long userId) throws UserException {
        User user = userRepository.findUserById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("User with id %s not found", userId))
        );
        Long loggedUserId = authenticationService.getUserId()
                .orElseThrow(() -> new UserException("User not logged in"));

        if (!loggedUserId.equals(userId)) throw new UserException(String.format(
                "User logged in with id '%s' does not match user delete request id '%s'", loggedUserId, userId
        ));
        List<UUID> urlsIds = shortUrlRepository.findAllByCreatedByUser(user)
                .stream().map(ShortUrl::getId).toList();
        shortUrlRepository.deleteByIdIn(urlsIds);
        userRepository.deleteById(userId);
        return urlsIds.size();
    }
}
