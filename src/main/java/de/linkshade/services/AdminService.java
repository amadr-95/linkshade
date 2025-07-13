package de.linkshade.services;

import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.dto.ShortUrlDTO;
import de.linkshade.domain.entities.dto.UserDTO;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
import de.linkshade.services.mapper.ShortUrlMapper;
import de.linkshade.services.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final PaginationService paginationService;
    private final ShortUrlMapper shortUrlMapper;
    private final UserMapper userMapper;

    public PagedResult<ShortUrlDTO> findAllShortUrls(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, shortUrlRepository::countAll);

        return PagedResult.from(shortUrlRepository.findAllUrls(validPage)
                .map(shortUrlMapper::toShortUrlDTO));
    }

    public PagedResult<UserDTO> findAllUsers(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, userRepository::countAll);

        return PagedResult.from(userRepository.findAllUsers(validPage)
                .map(userMapper::toUserDTO));
    }
}
