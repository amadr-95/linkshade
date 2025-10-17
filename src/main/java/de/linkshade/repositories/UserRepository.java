package de.linkshade.repositories;

import de.linkshade.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    @Query("select count(*) from User")
    Long countAll();

    @Query("select u from User u")
    Page<User> findAllUsers(Pageable pageable);

    @Query("""
    select u from User u
    left join ShortUrl s on s.createdByUser = u
    group by u.id
    order by count (s.id) asc
    """)
    Page<User> findAllUsersSortedByUrlCountAsc(Pageable pageable);

    @Query("""
    select u from User u
    left join ShortUrl s on s.createdByUser = u
    group by u.id
    order by count (s.id) desc
    """)
    Page<User> findAllUsersSortedByUrlCountDesc(Pageable pageable);


    Optional<User> findUserById(Long userId);

    int deleteByIdIn(Collection<Long> ids);

    Optional<User> findByUserProviderId(String userProviderId);
}
