package com.amador.urlshortener.services.mapper;

import com.amador.urlshortener.domain.entities.ShortUrl;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.domain.entities.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlMapper {

    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl) {
        return ShortUrlDTO.builder()
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
