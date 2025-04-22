package com.amador.urlshortener.domain.entities.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ShortUrlDTO(
        UUID id,
        String shortenedUrl,
        String originalUrl,
        UserDTO createdByUser,
        boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Long numberOfClicks
) {
}
