package com.amador.urlshortener.domain.entities.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ShortUrlDTO(
        String shortenedUrl,
        String originalUrl,
        UserDTO createdByUser,
        boolean isPrivate,
        LocalDateTime createdAt,
        LocalDateTime expiresAt,
        Long numberOfClicks
) {
}
