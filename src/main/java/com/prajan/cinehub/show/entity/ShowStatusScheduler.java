package com.prajan.cinehub.show.entity;

import com.prajan.cinehub.show.enums.ShowStatus;
import com.prajan.cinehub.show.repository.ShowRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ShowStatusScheduler {

    private final ShowRepository showRepository;

    @Scheduled(fixedRate = 60000) // every 1 minute
    @Transactional
    public void updateCompletedShows() {

        LocalDateTime now = LocalDateTime.now();

        List<Show> shows = showRepository.findAll();

        shows.stream()
                .filter(show -> show.getStatus() != ShowStatus.COMPLETED)
                .filter(show -> {

                    LocalDateTime showEndDateTime =
                            LocalDateTime.of(
                                    show.getShowDate(),
                                    show.getEndTime()
                            );

                    return showEndDateTime.isBefore(now);
                })
                .forEach(show ->
                        show.setStatus(ShowStatus.COMPLETED)
                );
        showRepository.saveAll(shows);
    }
}