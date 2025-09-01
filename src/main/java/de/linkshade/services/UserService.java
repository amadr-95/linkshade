package de.linkshade.services;

import de.linkshade.domain.entities.*;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UserException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.mapper.ShortUrlMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final AuthenticationService authenticationService;
    private final ShortUrlMapper shortUrlMapper;
    private final PaginationService paginationService;

    public PagedResult<ShortUrlDTO> getUserShortUrls(Pageable pageableRequest) {
        User currentUser = getCurrentUser();
        Pageable validPage = paginationService.createValidPage(pageableRequest,
                () -> shortUrlRepository.countByCreatedByUser(currentUser.getId()));
        Page<ShortUrlDTO> shortUrlDTOS =
                shortUrlRepository.findAllByCreatedByUser(validPage, currentUser.getId())
                        .map(shortUrlMapper::toShortUrlDTO);
        return PagedResult.from(shortUrlDTOS);
    }

    @Transactional
    public void deleteSelectedUrls(List<UUID> shortUrlsIds) throws UrlNotFoundException {
        getCurrentUser();
        if (shortUrlsIds.stream().anyMatch(Objects::isNull))
            throw new UrlNotFoundException("One or more URLs were null");
        shortUrlRepository.deleteByIdIn(shortUrlsIds);
    }

    //TODO: provide a custom exception
    private User getCurrentUser() {
        User currentUser = authenticationService.getCurrentUserInfo().user();
        if (currentUser == null)
            throw new UsernameNotFoundException("Username not found");
        return currentUser;
    }

    @Transactional
    public User registerUser(Map<String, Object> userAttributes, AuthProvider oAuthProvider) {

        User user = User.builder()
                .name(userAttributes.get("name").toString())
                .email(userAttributes.get("email") == null ? "Not provided" : userAttributes.get("email").toString())
                .userProviderId(userAttributes.get("id").toString())
                .authProvider(oAuthProvider)
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public int deleteUser(Long userId) throws UserException {
        User user = userRepository.findUserById(userId).orElseThrow(
                () -> new UsernameNotFoundException("Username not found")
        );
        Long loggedUserId = authenticationService.getUserId();
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
