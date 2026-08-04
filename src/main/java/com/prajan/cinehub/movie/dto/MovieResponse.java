package com.prajan.cinehub.movie.dto;

import com.prajan.cinehub.movie.enums.Certificate;
import com.prajan.cinehub.movie.enums.Genre;
import com.prajan.cinehub.movie.enums.Language;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class MovieResponse {

    private Long id;

    private String title;

    private String description;

    private Integer duration;

    private Genre genre;

    private Language language;

    private Certificate certificate;

    private LocalDate releaseDate;

    private String posterUrl;

    private Double imdbRating;

    private Boolean active;
}
