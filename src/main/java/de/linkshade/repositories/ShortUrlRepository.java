package de.linkshade.repositories;

import de.linkshade.domain.entities.ShortUrl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, UUID> {
    @Query("select su from ShortUrl su where su.isPrivate=false")
    @EntityGraph(attributePaths = {"createdByUser"})
    Page<ShortUrl> findAllPublicUrls(Pageable pageable);

    @Query("select su from ShortUrl su")
    @EntityGraph(attributePaths = {"createdByUser"})
    Page<ShortUrl> findAllUrls(Pageable pageable);

    boolean existsByShortenedUrl(String shortenedUrl);

    Optional<ShortUrl> findByShortenedUrl(String shortenedUrl);

    @Query("select count(su) from ShortUrl su where su.isPrivate=false")
    Long countAllPublicUrls();

    @Query("select count(*) from ShortUrl")
    Long countAll();

    @Query("select su from ShortUrl su where su.createdByUser.id=:userId")
    @EntityGraph(attributePaths = {"createdByUser"})
    Page<ShortUrl> findAllByCreatedByUser(Pageable pageable, UUID userId);

    @Query("select count(su) from ShortUrl su where su.createdByUser.id=:userId")
    Long countByCreatedByUser(UUID userId);

    @Modifying
    int deleteByIdInAndCreatedByUserId(List<UUID> ids, UUID userId);

    @Modifying
    int deleteByIdIn(List<UUID> ids);

    @Modifying
    @Query("delete from ShortUrl su where su.createdByUser.id in:userIds")
    int deleteByCreatedByUserIn(Collection<UUID> userIds);

    @Query("select su.id from ShortUrl su where su.createdByUser.id=:userId")
    List<UUID> findAllByCreatedByUserId(UUID userId);

    @Query("select su from ShortUrl su where su.createdByUser.id=:userId and su.expiresAt is not null")
    List<ShortUrl> findUrlsWithExpirationByUserId(UUID userId);

    @Modifying
    @Query("update ShortUrl su set su.expiresAt = :newDate where su.id in :ids")
    int updateExpirationDateByUrlIds(List<UUID> ids, LocalDate newDate);

    @Query("select su from ShortUrl su where su.createdByUser is null and su.expiresAt is not null")
    List<ShortUrl> findUrlsWithExpirationByUserNull();

    @Modifying
    @Query("delete from ShortUrl su where su.createdByUser is null and su.id in :ids")
    int deleteAllNonCreatedByUserExpiredUrls(List<UUID> ids);

}
