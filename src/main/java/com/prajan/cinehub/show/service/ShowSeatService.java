package com.prajan.cinehub.show.service;

import com.prajan.cinehub.show.dto.ShowSeatResponse;
import com.prajan.cinehub.show.entity.ShowSeat;
import com.prajan.cinehub.show.repository.ShowSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowSeatService {

    private final ShowSeatRepository showSeatRepository;

    public List<ShowSeatResponse> getSeatsByShow(Long showId) {

        return showSeatRepository.findByShowId(showId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ShowSeatResponse mapToResponse(ShowSeat showSeat) {

        return ShowSeatResponse.builder()
                .id(showSeat.getId())
                .seatId(showSeat.getSeat().getId())
                .rowName(showSeat.getSeat().getRowName())
                .seatNumber(showSeat.getSeat().getSeatNumber())
                .seatType(showSeat.getSeat().getSeatType())
                .status(showSeat.getStatus())
                .build();
    }
}