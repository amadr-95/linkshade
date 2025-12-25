package de.linkshade.services.mapper;

import de.linkshade.config.Constants;
import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlMapper {

    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl) {
        return ShortUrlDTO.builder()
                .id(shortUrl.getId())
                .shortenedUrl(shortUrl.getShortenedUrl())
                .originalUrl(shortUrl.getOriginalUrl())
                .createdByUser(shortUrl.getCreatedByUser() == null ?
                        Constants.DEFAULT_USER_NAME : shortUrl.getCreatedByUser().getName())
                .isPrivate(shortUrl.isPrivate())
                .shareCode(shortUrl.getShareCode())
                .createdAt(shortUrl.getCreatedAt())
                .expiresAt(shortUrl.getExpiresAt())
                .daysToExpire(shortUrl.daysToExpire())
                .isExpired(shortUrl.isExpired())
                .numberOfClicks(shortUrl.getNumberOfClicks())
                .build();
    }

}
