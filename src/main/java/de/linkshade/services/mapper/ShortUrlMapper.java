package de.linkshade.services.mapper;

import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.domain.entities.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Service
public class ShortUrlMapper {

    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl) {
        return ShortUrlDTO.builder()
                .id(shortUrl.getId())
                .shortenedUrl(shortUrl.getShortenedUrl())
                .originalUrl(shortUrl.getOriginalUrl())
                .createdByUser(UserDTO.builder()
                        .name(shortUrl.getCreatedByUser() == null ?
                                "Guest" : shortUrl.getCreatedByUser().getName())
                        .build())
                .isPrivate(shortUrl.isPrivate())
                .createdAt(shortUrl.getCreatedAt())
                .expiresAt(shortUrl.getExpiresAt())
                .daysToExpire(calculateDaysBetween(shortUrl.getExpiresAt()))
                .isExpired(isExpired(shortUrl.getExpiresAt()))
                .numberOfClicks(shortUrl.getNumberOfClicks())
                .build();
    }

    private Integer calculateDaysBetween(LocalDateTime expiresAt) {
        if (expiresAt == null) return null;
        return Period.between(LocalDate.now(), expiresAt.toLocalDate()).getDays(); //might be 0 (same day)
    }

    private boolean isExpired(LocalDateTime expiresAt) {
        Integer days = calculateDaysBetween(expiresAt);
        if (days == null) return false;
        return days < 0;
    }
}
