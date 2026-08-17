package com.prajan.cinehub.theatre.service;

import com.prajan.cinehub.theatre.entity.Screen;
import com.prajan.cinehub.theatre.entity.Seat;
import com.prajan.cinehub.theatre.enums.SeatType;
import com.prajan.cinehub.theatre.repository.SeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;

    public void generateSeats(Screen screen,
                              Integer rows,
                              Integer seatsPerRow) {

        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < rows; row++) {

            String rowName = String.valueOf((char) ('A' + row));

            for (int seatNo = 1; seatNo <= seatsPerRow; seatNo++) {

                Seat seat = Seat.builder()
                        .rowName(rowName)
                        .seatNumber(seatNo)
                        .seatType(SeatType.REGULAR)
                        .active(true)
                        .screen(screen)
                        .build();

                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
    }

}
