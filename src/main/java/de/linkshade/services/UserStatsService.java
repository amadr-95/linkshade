package de.linkshade.services;

import de.linkshade.repositories.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserStatsService {

    private final ShortUrlRepository shortUrlRepository;

    public int getExpiredUrlsCountByUserId(Long userId) {
        return shortUrlRepository.numberOfExpiredUrlsByUserId(userId);
    }

}
