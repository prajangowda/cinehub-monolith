package com.prajan.cinehub.movie.mapper;

import com.prajan.cinehub.movie.dto.CreateMovieRequest;
import com.prajan.cinehub.movie.dto.MovieResponse;
import com.prajan.cinehub.movie.entity.Movie;
import org.springframework.stereotype.Component;

@Component
public class MovieMapper {

    private MovieMapper() {
    }

    public static Movie toEntity(CreateMovieRequest request) {

        return Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .genre(request.getGenre())
                .language(request.getLanguage())
                .certificate(request.getCertificate())
                .releaseDate(request.getReleaseDate())
                .posterUrl(request.getPosterUrl())
                .imdbRating(request.getImdbRating())
                .active(true)
                .build();
    }

    public static MovieResponse toResponse(Movie movie) {

        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .certificate(movie.getCertificate())
                .releaseDate(movie.getReleaseDate())
                .posterUrl(movie.getPosterUrl())
                .imdbRating(movie.getImdbRating())
                .active(movie.getActive())
                .build();
    }
}
