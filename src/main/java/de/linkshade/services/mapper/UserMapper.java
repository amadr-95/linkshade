package de.linkshade.services.mapper;

import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserMapper {

    public UserDTO toUserDTO(User user, Long urlCount) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .authProvider(user.getAuthProvider())
                .userProviderId(user.getUserProviderId())
                .createdAt(user.getCreatedAt())
                .numberOfUrlsCreated(urlCount)
                .role(user.getRole())
                .build();
    }
}
