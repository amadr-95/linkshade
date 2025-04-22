package com.amador.urlshortener.domain.entities.dto;

import lombok.Builder;

@Builder
public record UserDTO(Long id, String name) {
}
