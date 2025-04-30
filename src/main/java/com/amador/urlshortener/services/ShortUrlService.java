package com.amador.urlshortener.services;

import com.amador.urlshortener.domain.entities.ShortUrl;
import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.repositories.ShortUrlRepository;
import com.amador.urlshortener.services.mapper.ShortUrlMapper;
import com.amador.urlshortener.web.controllers.dto.ShortUrlForm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlMapper shortUrlMapper;

    public List<ShortUrlDTO> findAllPublicUrls() {
        return shortUrlRepository.findAllPublicUrls().stream()
                .map(shortUrlMapper::toShortUrlDTO).toList();
    }

    @Transactional
    public ShortUrlDTO createShortUrl(ShortUrlForm shortUrlForm) throws Exception { //we already know the url is valid from the controller
        ShortUrl shortUrl = ShortUrl.builder()
                .originalUrl(shortUrlForm.originalUrl())
                .shortenedUrl(shortenUrl()) //TODO: make the short URL of a combination of letters from the originalUrl
                .createdByUser(null)
                .isPrivate(false)
                .numberOfClicks(0L)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        shortUrlRepository.save(shortUrl);
        return shortUrlMapper.toShortUrlDTO(shortUrl);
    }

    private String shortenUrl() throws Exception {
        Random random = new Random();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        //TODO: make this customizable by the user (pick the length they want)
        int shortUrlLength = 10;
        int maxAttempts = 5;
        StringBuilder shortUrl = new StringBuilder(shortUrlLength);

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            for (int i = 0; i < shortUrlLength; i++) {
                shortUrl.append(characters.charAt(random.nextInt(characters.length())));
            }

            //check if that combination of characters already exists in database
            if(!shortUrlRepository.existsByShortenedUrl(shortUrl.toString()))
                return shortUrl.toString();
        }
        throw new Exception(); //TODO: throw custom exception
    }
}
