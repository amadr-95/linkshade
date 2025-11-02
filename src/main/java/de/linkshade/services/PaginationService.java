package de.linkshade.services;

import de.linkshade.config.AppProperties;
import de.linkshade.domain.entities.User;
import de.linkshade.security.AuthenticationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

import static de.linkshade.domain.entities.Role.ADMIN;

@Service
@RequiredArgsConstructor
public class PaginationService {

    private final AppProperties appProperties;
    private final AuthenticationService authenticationService;
    private Sort defaultSort;

    @PostConstruct
    private void init() {
        this.defaultSort = Sort.by(Sort.Direction.DESC, "createdAt")
                // unique field (id) is needed to avoid problems when retrieving sorted records from the database
                .and(Sort.by("id").descending());
    }

    public Pageable createValidPage(Pageable pageableRequest, Supplier<Long> countFunction) {

        int pageSize = validatePageSize(pageableRequest.getPageSize());
        int pageNumber = validatePageNumber(pageableRequest, countFunction, pageSize);
        Sort sort = validateSort(pageableRequest.getSort());

        return PageRequest.of(pageNumber, pageSize, sort);
    }

    private int validatePageSize(int pageSize) {
        // only allow values that are predefined: [5, 10, 20, 50]
        return Arrays.stream(appProperties.pageAvailableSizes())
                .anyMatch(size -> size == pageSize) ?
                pageSize : appProperties.pageDefaultSize();
    }

    private int validatePageNumber(Pageable pageableRequest, Supplier<Long> countFunction, int pageSize) {
        int pageNumber = Math.max(pageableRequest.getPageNumber() - 1, 0);
        // calculate the number of max pages to handle wrong pageNumber values coming from frontend (url)
        long totalElements = countFunction.get();
        int maxPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
        if (pageNumber >= maxPages) pageNumber = maxPages - 1; //redirect to the last page available
        return pageNumber;
    }

    private Sort validateSort(Sort sortRequest) {
        Optional<User> userInfo = authenticationService.getUserInfo();
        if (sortRequest.isUnsorted() || userInfo.isEmpty())
            return defaultSort;

        //if the direction is something different from ASC or DESC, @PageableDefault in the controller makes it ASC by default, no need for validation
        Sort.Order validSortProperty = sortRequest.stream()
                .filter(sort -> appProperties.urlSortProperties().contains(sort.getProperty()) ||
                        (userInfo.get().getRole() == ADMIN &&
                                appProperties.userSortProperties().contains(sort.getProperty()))
                )
                .findFirst()
                .orElse(null);

        return validSortProperty == null ?
                defaultSort :
                Sort.by(validSortProperty)
                        .and(Sort.by("id").descending());
    }
}
