package com.prajan.cinehub.movie.controller;

import com.prajan.cinehub.movie.dto.MoviePageResponse;
import com.prajan.cinehub.movie.dto.MovieResponse;
import com.prajan.cinehub.movie.entity.Movie;
import com.prajan.cinehub.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/movies")
@RequiredArgsConstructor
@Slf4j
public class MovieController {

    private final MovieService movieService;

    @GetMapping("/{movieId}")
    public MovieResponse getMovieById(@PathVariable Long movieId) {
        log.info("Getting movie for id {}");
        return movieService.getMovieById(movieId);
    }

    @GetMapping
    public ResponseEntity<MoviePageResponse> getAllMovies(
            @PageableDefault(
                    page = 0,
                    size = 12,
                    sort = "id"
            ) Pageable pageable
    ) {
        log.info("Getting movies ");
        return ResponseEntity.ok(
                movieService.getMovies(pageable)
        );
    }
}
