package com.amador.urlshortener.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "short_urls")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
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

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Long numberOfClicks;
}
