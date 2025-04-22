package com.amador.urlshortener.repositories;

import com.amador.urlshortener.domain.entities.ShortUrl;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {
//    @Query("select su from ShortUrl su left join fetch su.createdByUser where su.isPrivate=false order by su.createdAt desc")
    @Query("select su from ShortUrl su where su.isPrivate=false order by su.createdAt desc")
    @EntityGraph(attributePaths = {"createdByUser"})
    List<ShortUrl> findAllPublicUrls();
}
