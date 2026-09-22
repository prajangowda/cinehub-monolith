package com.prajan.cinehub.show.dto;

import com.prajan.cinehub.show.enums.SeatStatus;
import com.prajan.cinehub.theatre.enums.SeatType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowSeatResponse {

    private Long id;

    private Long seatId;

    private String rowName;

    private Integer seatNumber;

    private SeatType seatType;

    private SeatStatus status;
}