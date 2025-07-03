package de.linkshade.domain.entities.dto;

import de.linkshade.domain.entities.Role;
import lombok.Builder;

@Builder
public record UserDTO(
        Long id,
        String name,
        String email,
        Role role) {
}
