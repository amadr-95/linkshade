package de.linkshade.services;

import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.ShortUrl;
import de.linkshade.domain.entities.dto.UserDTO;
import de.linkshade.exceptions.UserException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
import de.linkshade.repositories.UserWithUrlCount;
import de.linkshade.services.mapper.UserMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final PaginationService paginationService;
    private final UserMapper userMapper;

    /**
     * Fetches all users with pagination support.
     * Uses optimized queries with JOIN to avoid N+1 query problem when counting URLs per user.
     * Special handling for sorting by numberOfUrlsCreated since it's a calculated field, not a table column.
     */
    public PagedResult<UserDTO> findAllUsers(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, userRepository::countAll);

        // Since numberOfUrlCreated is not a field in User table, it needs to be treated with specific queries in the repository
        Sort.Order orderForNumberOfUrlsCreated = validPage.getSort().getOrderFor("numberOfUrlsCreated");

        if (orderForNumberOfUrlsCreated == null) {
            return PagedResult.from(userRepository.findAllUsersWithUrlCount(validPage)
                    .map(result -> userMapper.toUserDTO(result.getUser(), result.getUrlCount())));
        }

        Pageable pageWithoutSort = PageRequest.of(validPage.getPageNumber(), validPage.getPageSize());
        Page<UserWithUrlCount> userPage = orderForNumberOfUrlsCreated.isAscending()
                ? userRepository.findAllUsersWithUrlCountSortedAsc(pageWithoutSort)
                : userRepository.findAllUsersWithUrlCountSortedDesc(pageWithoutSort);

        return PagedResult.from(userPage.map(result ->
                userMapper.toUserDTO(result.getUser(), result.getUrlCount())));
    }

    @Transactional
    public DeletionResult deleteSelectedUsers(List<UUID> userIds) throws UserException {
        if (userIds.stream().anyMatch(Objects::isNull))
            throw new UserException("One or more users were null");
        int urlsDeleted = shortUrlRepository.deleteByCreatedByUserIn(userIds);
        int usersDeleted = userRepository.deleteByIdIn(userIds);
        log.info("Batch users deletion: {} users and {} urls were deleted", usersDeleted, urlsDeleted);
        return new DeletionResult(usersDeleted, urlsDeleted);
    }

    @Transactional
    public int deleteAllNonCreatedByUserExpiredUrls() {
        int deletedCount = shortUrlRepository.deleteAllNonCreatedByUserExpiredUrls(getExpiredUrlsByUserNull());
        log.debug("Batch of non-created-by-user expired urls deletion: {} expired URLs were deleted", deletedCount);
        return deletedCount;
    }

    public List<UUID> getExpiredUrlsByUserNull() {
        List<ShortUrl> urlsWithExpiration = shortUrlRepository.findUrlsWithExpirationByUserNull();

        return urlsWithExpiration.stream()
                .filter(ShortUrl::isExpired)
                .map(ShortUrl::getId)
                .toList();
    }
}
