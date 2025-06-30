package com.amador.linkshade.domain.entities.dto;

import com.amador.linkshade.domain.entities.Role;
import lombok.Builder;

@Builder
public record UserDTO(
        Long id,
        String name,
        String email,
        Role role) {
}
