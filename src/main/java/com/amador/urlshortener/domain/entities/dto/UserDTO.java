package com.amador.urlshortener.domain.entities.dto;

import com.amador.urlshortener.domain.entities.Role;
import lombok.Builder;

@Builder
public record UserDTO(
        Long id,
        String name,
        String email,
        Role role) {
}
