package de.linkshade.services.mapper;

import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.domain.entities.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlMapper {

    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl) {
        return ShortUrlDTO.builder()
                .id(shortUrl.getId())
                .shortenedUrl(shortUrl.getShortenedUrl())
                .originalUrl(shortUrl.getOriginalUrl())
                .createdByUser(UserDTO.builder()
                        .name(shortUrl.getCreatedByUser() == null ?
                                "Guest": shortUrl.getCreatedByUser().getName())
                        .build())
                .isPrivate(shortUrl.isPrivate())
                .createdAt(shortUrl.getCreatedAt())
                .expiresAt(shortUrl.getExpiresAt())
                .numberOfClicks(shortUrl.getNumberOfClicks())
                .build();
    }
}
