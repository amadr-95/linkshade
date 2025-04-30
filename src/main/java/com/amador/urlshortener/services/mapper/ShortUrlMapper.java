package com.amador.urlshortener.services.mapper;

import com.amador.urlshortener.domain.entities.ShortUrl;
import com.amador.urlshortener.domain.entities.User;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.domain.entities.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class ShortUrlMapper {

    public ShortUrlDTO toShortUrlDTO(ShortUrl shortUrl) {
        UserDTO userDTO = null;
        User user = shortUrl.getCreatedByUser();
        if(user != null) {
            userDTO = toUserDTO(user);
        }

        return ShortUrlDTO.builder()
                .shortenedUrl(shortUrl.getShortenedUrl())
                .originalUrl(shortUrl.getOriginalUrl())
                .createdByUser(userDTO)
                .isPrivate(shortUrl.isPrivate())
                .createdAt(shortUrl.getCreatedAt())
                .expiresAt(shortUrl.getExpiresAt())
                .numberOfClicks(shortUrl.getNumberOfClicks())
                .build();
    }

    private UserDTO toUserDTO(User user) {
        return UserDTO.builder()
                .name(user.getName())
                .build();
    }
}
