package com.prajan.cinehub.movie.controller;

import com.prajan.cinehub.movie.dto.CreateMovieRequest;
import com.prajan.cinehub.movie.dto.UpdateMovieRequest;
import com.prajan.cinehub.movie.dto.MovieResponse;
import com.prajan.cinehub.movie.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse createMovie(
            @RequestPart("movie") CreateMovieRequest request,
            @RequestPart("poster") MultipartFile poster) {
        log.info("admin controller started");
        return movieService.createMovie(request,poster );
    }

    @PutMapping("/{movieId}")
    public MovieResponse updateMovie(
            @PathVariable Long movieId,
            @RequestBody UpdateMovieRequest request) {

        return movieService.updateMovie(movieId, request);
    }

    @DeleteMapping("/{movieId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMovie(@PathVariable Long movieId) {
        movieService.deleteMovie(movieId);
    }
}