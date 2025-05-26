package com.amador.urlshortener.repositories;

import com.amador.urlshortener.domain.entities.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {
//    @Query("select su from ShortUrl su left join fetch su.createdByUser where su.isPrivate=false order by su.createdAt desc")
    @Query("select su from ShortUrl su where su.isPrivate=false")
    @EntityGraph(attributePaths = {"createdByUser"})
    Page<ShortUrl> findAllPublicUrls(Pageable pageable);

    boolean existsByShortenedUrl(String shortenedUrl);

    Optional<ShortUrl> findByShortenedUrl(String shortenedUrl);

    @Query("select count(su) from ShortUrl su where su.isPrivate=false")
    Long countAllPublicUrls();
}
