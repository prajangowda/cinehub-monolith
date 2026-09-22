package com.prajan.cinehub.show.repository;

import com.prajan.cinehub.show.entity.Show;
import com.prajan.cinehub.show.enums.ShowStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface ShowRepository extends JpaRepository<Show, Long> {

    List<Show> findByScreenIdAndShowDate(
            Long screenId,
            LocalDate showDate
    );

    List<Show> findByMovieIdAndShowDate(
            Long movieId,
            LocalDate showDate
    );

    List<Show> findByStatus(ShowStatus status);

    List<Show> findByEndTimeBeforeAndStatusNot(
            LocalTime time,
            ShowStatus status
    );

    List<Show> findByMovieIdAndStatus(
            Long movieId,
            ShowStatus status
    );
}