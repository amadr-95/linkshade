package de.linkshade.services.mapper;

import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
//                .numberOfUrlsCreated(user)
                .build();
    }
}
