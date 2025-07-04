package de.linkshade.services;

import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.Role;
import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.exceptions.UrlNotFoundException;
import de.linkshade.exceptions.UserEmailDuplicateException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
import de.linkshade.security.AuthenticationService;
import de.linkshade.services.mapper.ShortUrlMapper;
import de.linkshade.web.controllers.dto.UserRegistrationRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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

    private User getCurrentUser() {
        User currentUser = authenticationService.getCurrentUserInfo();
        if (currentUser == null)
            throw new UsernameNotFoundException("Username not found");
        return currentUser;
    }

    @Transactional
    public void registerUser(UserRegistrationRequest userRequest) throws UserEmailDuplicateException {

        if (userRepository.existsByEmail(userRequest.email()))
            throw new UserEmailDuplicateException(String.format("User with email '%s' already exists", userRequest.email()));

        User user = User.builder()
                .email(userRequest.email())
                .password(passwordEncoder.encode(userRequest.password()))
                .name(userRequest.name())
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);
    }
}
