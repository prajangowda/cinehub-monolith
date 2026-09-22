package com.prajan.cinehub.booking.dto;

import java.math.BigDecimal;

public record ReservationResponse(

        String reservationToken,

        BigDecimal totalAmount,

        long expiresInSeconds

) {
}