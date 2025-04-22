package com.amador.urlshortener.services;

import com.amador.urlshortener.domain.entities.dto.ShortUrlDTO;
import com.amador.urlshortener.repositories.ShortUrlRepository;
import com.amador.urlshortener.services.mapper.ShortUrlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlMapper shortUrlMapper;

    public List<ShortUrlDTO> findAllPublicUrls() {
        return shortUrlRepository.findAllPublicUrls().stream()
                .map(shortUrlMapper::toShortUrlDTO).toList();
    }
}
