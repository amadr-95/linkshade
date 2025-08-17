package de.linkshade.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @Column(nullable = false, name = "shortened_url")
    private String shortenedUrl;

    @Column(nullable = false)
    private String originalUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user", foreignKey = @ForeignKey(name = "fk_user"))
    private User createdByUser;

    private boolean isPrivate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    private LocalDate expiresAt;

    @Column(nullable = false)
    private Long numberOfClicks;

    public Integer daysToExpire() {
        if (this.expiresAt == null) return null;
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), this.expiresAt);//might be 0 (same day)
    }

    public boolean isExpired() {
        Integer days = daysToExpire();
        if (days == null) return false;
        return days < 0;
    }
}
