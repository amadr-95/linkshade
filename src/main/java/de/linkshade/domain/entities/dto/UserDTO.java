package de.linkshade.domain.entities.dto;

import de.linkshade.domain.entities.AuthProvider;
import de.linkshade.domain.entities.Role;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record UserDTO(
        UUID id,

        String name,

        String email,

        String userProviderId,

        AuthProvider authProvider,

        Instant createdAt,

        Long numberOfUrlsCreated,

        Role role) {
}
