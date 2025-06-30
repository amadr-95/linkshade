package com.amador.linkshade.services;

import com.amador.linkshade.domain.entities.PagedResult;
import com.amador.linkshade.domain.entities.User;
import com.amador.linkshade.domain.entities.dto.ShortUrlDTO;
import com.amador.linkshade.exceptions.UrlNotFoundException;
import com.amador.linkshade.repositories.ShortUrlRepository;
import com.amador.linkshade.security.AuthenticationService;
import com.amador.linkshade.services.mapper.ShortUrlMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ShortUrlRepository shortUrlRepository;
    private final AuthenticationService authenticationService;
    private final ShortUrlMapper shortUrlMapper;
    private final PaginationService paginationService;

    public PagedResult<ShortUrlDTO> getUserShortUrls(Pageable pageableRequest) {
        User currentUser = getCurrentUser();
        Pageable validPage = paginationService.createValidPage(pageableRequest,
                () -> shortUrlRepository.countByCreatedByUser(currentUser.getId()));
        Page<ShortUrlDTO> shortUrlDTOS =
                shortUrlRepository.findAllByCreatedByUser(validPage, currentUser.getId())
                        .map(shortUrlMapper::toShortUrlDTO);
        return PagedResult.from(shortUrlDTOS);
    }

    @Transactional
    public void deleteSelectedUrls(List<UUID> shortUrlsIds) throws UrlNotFoundException {
        getCurrentUser();
        if (shortUrlsIds.stream().anyMatch(Objects::isNull))
            throw new UrlNotFoundException("One or more URLs were null");
        shortUrlRepository.deleteByIdIn(shortUrlsIds);
    }

    private User getCurrentUser() {
        User currentUser = authenticationService.getCurrentUserInfo();
        if (currentUser == null)
            throw new UsernameNotFoundException("Username not found");
        return currentUser;
    }
}
