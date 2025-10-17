package de.linkshade.services;

import de.linkshade.domain.entities.PagedResult;
import de.linkshade.domain.entities.User;
import de.linkshade.domain.entities.dto.UserDTO;
import de.linkshade.exceptions.UserException;
import de.linkshade.repositories.ShortUrlRepository;
import de.linkshade.repositories.UserRepository;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final ShortUrlRepository shortUrlRepository;
    private final UserRepository userRepository;
    private final PaginationService paginationService;
    private final UserMapper userMapper;

    public PagedResult<UserDTO> findAllUsers(Pageable pageableRequest) {
        Pageable validPage = paginationService.createValidPage(pageableRequest, userRepository::countAll);

        // Since numberOfUrlCreated it is not a field of User table, it needs to be treated with specific queries in the repository
        Sort.Order orderForNumberOfUrlsCreated = validPage.getSort().getOrderFor("numberOfUrlsCreated");

        if (orderForNumberOfUrlsCreated == null)
            return PagedResult.from(userRepository.findAllUsers(validPage)
                    .map(userMapper::toUserDTO));

        Pageable pageWithoutSort = PageRequest.of(validPage.getPageNumber(), validPage.getPageSize());
        Page<User> userPage = orderForNumberOfUrlsCreated.isAscending()
                ? userRepository.findAllUsersSortedByUrlCountAsc(pageWithoutSort)
                : userRepository.findAllUsersSortedByUrlCountDesc(pageWithoutSort);

        return PagedResult.from(userPage.map(userMapper::toUserDTO));
    }

    @Transactional
    public int[] deleteSelectedUsers(List<Long> userIds) throws UserException {
        if (userIds.stream().anyMatch(Objects::isNull))
            throw new UserException("One or more Users were null");
        int urlsDeleted = shortUrlRepository.deleteByCreatedByUserIn(userIds);
        int usersDeleted = userRepository.deleteByIdIn(userIds);
        log.info("{} users and {} urls were deleted", usersDeleted, urlsDeleted);
        return new int[]{usersDeleted, urlsDeleted};
    }
}
