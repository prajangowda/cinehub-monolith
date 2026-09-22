package com.prajan.cinehub.show.service;

import com.prajan.cinehub.error.ResourceNotFoundException;
import com.prajan.cinehub.movie.entity.Movie;
import com.prajan.cinehub.movie.repository.MovieRepository;
import com.prajan.cinehub.show.dto.CreateShowRequest;
import com.prajan.cinehub.show.dto.ShowResponse;
import com.prajan.cinehub.show.dto.UpdateShowRequest;
import com.prajan.cinehub.show.entity.Show;
import com.prajan.cinehub.show.entity.ShowSeat;
import com.prajan.cinehub.show.enums.SeatStatus;
import com.prajan.cinehub.show.enums.ShowStatus;
import com.prajan.cinehub.show.mapper.ShowMapper;
import com.prajan.cinehub.show.repository.ShowRepository;
import com.prajan.cinehub.show.repository.ShowSeatRepository;
import com.prajan.cinehub.theatre.entity.Screen;
import com.prajan.cinehub.theatre.repository.ScreenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieRepository movieRepository;
    private final ScreenRepository screenRepository;
    private final ShowSeatRepository showSeatRepository;

    @Transactional
    public ShowResponse createShow(CreateShowRequest request) {

        validateTime(request);

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Movie not found"));

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Screen not found"));

        validateScreenAvailability(
                screen.getId(),
                request.getShowDate(),
                request.getStartTime(),
                request.getEndTime()
        );

        Show show = Show.builder()
                .movie(movie)
                .screen(screen)
                .showDate(request.getShowDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ShowStatus.SCHEDULED)
                .active(true)
                .build();

        Show savedShow = showRepository.save(show);

        List<ShowSeat> showSeats = savedShow.getScreen()
                .getSeats()
                .stream()
                .filter(seat -> Boolean.TRUE.equals(seat.getActive()))
                .map(seat -> ShowSeat.builder()
                        .show(savedShow)
                        .seat(seat)
                        .status(SeatStatus.AVAILABLE)
                        .build())
                .toList();

        showSeatRepository.saveAll(showSeats);

        return ShowMapper.toResponse(savedShow);
    }

    private void validateTime(CreateShowRequest request) {

        if (request.getShowDate() == null ||
                request.getStartTime() == null ||
                request.getEndTime() == null) {

            throw new IllegalArgumentException(
                    "Date and show time are required"
            );
        }

        if (!request.getEndTime().isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        if (request.getShowDate().isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Show date cannot be in the past"
            );
        }
    }

    public ShowResponse getShowById(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Show not found"));

        return ShowMapper.toResponse(show);
    }

    private void validateScreenAvailability(
            Long screenId,
            LocalDate showDate,
            java.time.LocalTime startTime,
            java.time.LocalTime endTime) {

        List<Show> shows =
                showRepository.findByScreenIdAndShowDate(
                        screenId,
                        showDate
                );

        boolean conflict = shows.stream()
                .filter(show ->
                        show.getStatus() != ShowStatus.CANCELLED)
                .anyMatch(show ->
                        startTime.isBefore(show.getEndTime())
                                &&
                                endTime.isAfter(show.getStartTime())
                );

        if (conflict) {
            throw new IllegalArgumentException(
                    "Screen already has a show during this time"
            );
        }
    }

    public ShowResponse updateShow(
            Long showId,
            UpdateShowRequest request) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Show not found"));

        if (show.getStatus() == ShowStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled show cannot be updated"
            );
        }

        if (!request.getEndTime()
                .isAfter(request.getStartTime())) {

            throw new IllegalArgumentException(
                    "End time must be after start time"
            );
        }

        validateScreenAvailabilityForUpdate(
                show,
                request
        );

        show.setShowDate(request.getShowDate());
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime());

        return ShowMapper.toResponse(show);
    }

    private void validateScreenAvailabilityForUpdate(
            Show currentShow,
            UpdateShowRequest request) {

        List<Show> shows =
                showRepository.findByScreenIdAndShowDate(
                        currentShow.getScreen().getId(),
                        request.getShowDate()
                );

        boolean conflict = shows.stream()
                .filter(show -> !show.getId()
                        .equals(currentShow.getId()))
                .filter(show ->
                        show.getStatus() != ShowStatus.CANCELLED)
                .anyMatch(show ->
                        request.getStartTime()
                                .isBefore(show.getEndTime())
                                &&
                                request.getEndTime()
                                        .isAfter(show.getStartTime())
                );

        if (conflict) {
            throw new IllegalArgumentException(
                    "Screen already has a show during this time"
            );
        }
    }

    public void cancelShow(Long showId) {

        Show show = showRepository.findById(showId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Show not found"));

        show.setStatus(ShowStatus.CANCELLED);
        show.setActive(false);
    }

    public List<ShowResponse> getAvailableShowsByMovie(Long movieId) {

        List<Show> shows = showRepository.findByMovieIdAndStatus(
                movieId,
                ShowStatus.SCHEDULED
        );

        return shows.stream()
                .map(show -> ShowResponse.builder()
                        .id(show.getId())

                        .movieId(show.getMovie().getId())
                        .movieTitle(show.getMovie().getTitle())

                        .screenId(show.getScreen().getId())
                        .screenName(show.getScreen().getName())

                        .theatreId(show.getScreen().getTheatre().getId())
                        .theatreName(show.getScreen().getTheatre().getName())

                        .showDate(show.getShowDate())
                        .startTime(show.getStartTime())
                        .endTime(show.getEndTime())

                        .status(show.getStatus())
                        .active(show.getActive())
                        .build())
                .toList();
    }
}