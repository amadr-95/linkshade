package de.linkshade.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    private Instant createdAt;
}
