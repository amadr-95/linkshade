package de.linkshade.domain.entities.dto;

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
        boolean isExpired,
        Integer daysToExpire,
        Long numberOfClicks
) {
}
