package de.linkshade.services.mapper;

import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.UserDTO;
import de.linkshade.repositories.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    private final ShortUrlRepository shortUrlRepository;

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .authProvider(user.getAuthProvider())
                .userProviderId(user.getUserProviderId())
                .createdAt(user.getCreatedAt())
                .numberOfUrlsCreated(shortUrlRepository.countByCreatedByUser(user.getId()))
                .role(user.getRole())
                .build();
    }
}
