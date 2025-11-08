package de.linkshade.repositories;

import de.linkshade.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findUserByEmail(String email);

    @Query("select count(*) from User")
    Long countAll();

    /**
     * Fetches all users with their URL count using a single query with LEFT JOIN.
     * This avoids the N+1 query problem that would occur if we fetched users
     * and then counted URLs separately for each user.
     */
    @Query("""
    select u as user, count(s.id) as urlCount from User u
    left join ShortUrl s on s.createdByUser = u
    group by u.id
    """)
    Page<UserWithUrlCount> findAllUsersWithUrlCount(Pageable pageable);

    /**
     * Same as findAllUsersWithUrlCount but sorted by URL count in ascending order.
     * Sorting is done in the query itself since numberOfUrlsCreated is not a User field.
     */
    @Query("""
    select u as user, count(s.id) as urlCount from User u
    left join ShortUrl s on s.createdByUser = u
    group by u.id
    order by count(s.id) asc
    """)
    Page<UserWithUrlCount> findAllUsersWithUrlCountSortedAsc(Pageable pageable);

    /**
     * Same as findAllUsersWithUrlCount but sorted by URL count in descending order.
     */
    @Query("""
    select u as user, count(s.id) as urlCount from User u
    left join ShortUrl s on s.createdByUser = u
    group by u.id
    order by count(s.id) desc
    """)
    Page<UserWithUrlCount> findAllUsersWithUrlCountSortedDesc(Pageable pageable);

    Optional<User> findUserById(UUID userId);

    @Modifying
    int deleteByIdIn(Collection<UUID> ids);

    Optional<User> findByUserProviderId(String userProviderId);
}
