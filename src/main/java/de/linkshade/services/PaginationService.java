package de.linkshade.services;

import de.linkshade.config.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class PaginationService {

    private final AppProperties appProperties;

    public Pageable createValidPage(Pageable pageableRequest, Supplier<Long> countFunction) {
        // only allow values that are predefined: [5, 10, 20, 50]
        int pageSize = Arrays.stream(appProperties.pageAvailableSizes())
                .anyMatch(size -> size == pageableRequest.getPageSize()) ?
                pageableRequest.getPageSize() : appProperties.pageDefaultSize();

        int pageNumber = Math.max(pageableRequest.getPageNumber() - 1, 0);
        // calculate the number of max pages to handle wrong pageNumber values coming from frontend (url)
        long totalElements = countFunction.get();
        int maxPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);
        if (pageNumber >= maxPages) pageNumber = maxPages - 1; //redirect to the last page available

        return PageRequest
                .of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
