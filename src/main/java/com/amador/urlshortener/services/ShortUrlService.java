package com.amador.urlshortener.services;

import com.amador.urlshortener.domain.entities.ShortUrl;
import com.amador.urlshortener.repositories.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;

    public List<ShortUrl> findAllPublicUrls() {
        return shortUrlRepository.findAllPublicUrls();
    }
}
