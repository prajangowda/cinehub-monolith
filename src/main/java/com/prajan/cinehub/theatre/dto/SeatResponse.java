package com.prajan.cinehub.theatre.dto;

import com.prajan.cinehub.theatre.enums.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SeatResponse {

    private Long id;

    private String rowName;

    private Integer seatNumber;

    private SeatType seatType;

    private Boolean active;
}