package de.linkshade.services;

import de.linkshade.domain.entities.AuthProvider;
import de.linkshade.domain.entities.Role;
import de.linkshade.domain.entities.User;
import de.linkshade.exceptions.UserNotFoundException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
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
    public int deleteUser(UUID userId) throws UserNotFoundException {
        userRepository.findUserById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id: %s not found", userId)));

        List<UUID> urlsIds = shortUrlRepository.findAllByCreatedByUserId(userId);
        if (urlsIds.isEmpty()) {
            userRepository.deleteById(userId);
            return 0;
        }
        int deletedUrls = shortUrlRepository.deleteByIdInAndCreatedByUserId(urlsIds, userId);
        userRepository.deleteById(userId);
        return deletedUrls;
    }
}
