package de.linkshade.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.SEQUENCE;

@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "emailUnique", columnNames = "email"),
                @UniqueConstraint(name = "providerIdUnique", columnNames = "user_provider_id")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1)
    @GeneratedValue(strategy = SEQUENCE, generator = "user_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @ToString.Exclude
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    /**
     * unique user identifier (id) provided by the authentication provider (Google, GitHub...)
     */
    @Column(nullable = false)
    private String userProviderId;

    @ToString.Exclude
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @ToString.Exclude
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ToString.Exclude
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;
}
