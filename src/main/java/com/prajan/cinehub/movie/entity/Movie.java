package com.prajan.cinehub.movie.entity;

import com.prajan.cinehub.movie.enums.Certificate;
import com.prajan.cinehub.movie.enums.Genre;
import com.prajan.cinehub.movie.enums.Language;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "movies",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_movie_title_language_release",
                        columnNames = {"title", "language", "releaseDate"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 3000, nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer duration; // minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Genre genre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Certificate certificate;

    @Column(nullable = false)
    private LocalDate releaseDate;

    @Column(nullable = false)
    private String posterUrl;

    private Double imdbRating;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
