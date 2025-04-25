package com.amador.urlshortener.services.mapper;

import com.amador.urlshortener.domain.entities.ShortUrl;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlMapper {

    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl) {
        return ShortUrlDTO.builder()
                .shortenedUrl(shortUrl.getShortenedUrl())
                .originalUrl(shortUrl.getOriginalUrl())
                .createdByUser(shortUrl.getCreatedByUser().getName())
                .isPrivate(shortUrl.isPrivate())
                .createdAt(shortUrl.getCreatedAt())
                .expiresAt(shortUrl.getExpiresAt())
                .numberOfClicks(shortUrl.getNumberOfClicks())
                .build();
    }
}
