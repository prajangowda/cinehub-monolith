package com.prajan.cinehub.movie.service;

import com.prajan.cinehub.movie.dto.CreateMovieRequest;
import com.prajan.cinehub.movie.dto.MoviePageResponse;
import com.prajan.cinehub.movie.dto.MovieResponse;
import com.prajan.cinehub.movie.dto.UpdateMovieRequest;
import com.prajan.cinehub.movie.entity.Movie;
import com.prajan.cinehub.movie.mapper.MovieMapper;
import com.prajan.cinehub.movie.repository.MovieRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieService {

    private final MovieRepository movieRepository;
    private final MovieMapper movieMapper;
    private final SupabaseStorageService supabaseStorageService;

    @Transactional
    public MovieResponse createMovie(
            CreateMovieRequest request,
            MultipartFile poster
    ) {


        String imageUrl = supabaseStorageService.uploadImage(poster);
       log.info("image uploaded");
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .duration(request.getDuration())
                .releaseDate(request.getReleaseDate())
                .language(request.getLanguage())
                .genre(request.getGenre())
                .certificate(request.getCertificate())
                .posterUrl(imageUrl)
                .imdbRating(request.getImdbRating())
                .active(true)
                .build();

        movie = movieRepository.save(movie);

        return movieMapper.toResponse(movie);
    }

    @Transactional
    public MovieResponse updateMovie(
            Long movieId,
            UpdateMovieRequest request
    ) {
        // TODO: Implement
        return null;
    }

    @Transactional
    public void deleteMovie(Long movieId) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Movie not found with id: " + movieId
                        )
                );

        movieRepository.delete(movie);

        log.info("Movie permanently deleted successfully. Movie ID: {}", movieId);
    }


    public MovieResponse getMovieById(Long movieId) {

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Movie not found with id: " + movieId));

        return mapToResponse(movie);
    }




    public List<MovieResponse> getAllMovies() {

        return movieRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public MoviePageResponse getMovies(Pageable pageable) {

        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<MovieResponse> movies = moviePage.getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        return MoviePageResponse.builder()
                .content(movies)
                .page(moviePage.getNumber())
                .size(moviePage.getSize())
                .totalElements(moviePage.getTotalElements())
                .totalPages(moviePage.getTotalPages())
                .first(moviePage.isFirst())
                .last(moviePage.isLast())
                .build();
    }
    private MovieResponse mapToResponse(Movie movie) {
        return MovieResponse.builder()
                .id(movie.getId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .genre(movie.getGenre())
                .language(movie.getLanguage())
                .certificate(movie.getCertificate())
                .releaseDate(movie.getReleaseDate())
                .imdbRating(movie.getImdbRating())
                .posterUrl(movie.getPosterUrl())
                .build();
    }
}