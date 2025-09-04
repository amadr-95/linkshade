package de.linkshade.domain.entities.dto;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ShortUrlDTO(
        UUID id,
        String shortenedUrl,
        String originalUrl,
        String createdByUser,
        boolean isPrivate,
        LocalDate createdAt,
        LocalDate expiresAt,
        boolean isExpired,
        Integer daysToExpire,
        Long numberOfClicks
) {
}
