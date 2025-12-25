package de.linkshade.domain.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Entity
@Table(name = "short_urls", uniqueConstraints =
        @UniqueConstraint(name = "shortUrlUnique", columnNames = {"shortened_url"})
)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String shortenedUrl;

    @Column(nullable = false)
    private String originalUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user", foreignKey = @ForeignKey(name = "fk_user"))
    private User createdByUser;

    private boolean isPrivate;

    private String shareCode;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private Instant lastModifiedAt;

    private LocalDate expiresAt;

    @Column(nullable = false, updatable = false)
    private String zoneId;

    @Column(nullable = false)
    private Long numberOfClicks;

    public Integer daysToExpire() {
        if (this.expiresAt == null) return null;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(ZoneId.of(this.zoneId)), this.expiresAt);
    }

    public boolean isExpired() {
        if (this.expiresAt == null) return false;
        return LocalDate.now(ZoneId.of(this.zoneId)).isAfter(this.expiresAt);
    }
}
