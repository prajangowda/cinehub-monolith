package com.prajan.cinehub.booking.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
public class MyBookingResponse {

    private Long bookingId;

    private String bookingReference;

    private String status;

    private BigDecimal totalAmount;

    private LocalDate showDate;

    private LocalTime startTime;

    private String movieTitle;

    private List<String> seats;
}