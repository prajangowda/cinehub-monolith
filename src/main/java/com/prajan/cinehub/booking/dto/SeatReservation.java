package com.prajan.cinehub.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservation {

    private String reservationToken;

    private Long showId;

    private List<Long> showSeatIds;

    private Long userId;

    private BigDecimal totalAmount;
}