package com.prajan.cinehub.movie.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MovieSummaryResponse {

    private Long id;

    private String title;

    private String posterUrl;

    private Double imdbRating;
}