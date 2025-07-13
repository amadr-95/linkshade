package de.linkshade.domain.entities.dto;

import de.linkshade.domain.entities.Role;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserDTO(
        Long id,
        String name,
        String email,
        String password,
        LocalDateTime createdAt,
        Long numberOfUrlsCreated,
        Role role) {
}
