package com.prajan.cinehub.show.repository;

import com.prajan.cinehub.show.entity.ShowSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {

    List<ShowSeat> findByShowId(Long showId);

    Optional<ShowSeat> findByShowIdAndSeatId(
            Long showId,
            Long seatId
    );

    List<ShowSeat> findByIdIn(List<Long> ids);



}