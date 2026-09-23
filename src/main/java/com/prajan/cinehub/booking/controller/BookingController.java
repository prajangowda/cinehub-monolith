package com.prajan.cinehub.booking.controller;

import com.prajan.cinehub.auth.model.CustomUserDetails;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.booking.dto.CreateBookingRequest;
import com.prajan.cinehub.booking.dto.MyBookingResponse;
import com.prajan.cinehub.booking.dto.ReservationResponse;
import com.prajan.cinehub.booking.service.BookingService;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;


    @PostMapping("/reserve")
    public ResponseEntity<ReservationResponse> reserveSeats(
            @RequestBody CreateBookingRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        log.info("Reserving seats for user: {}", userDetails.getUsername());
        ReservationResponse response =
                bookingService.reserveSeats(request, userDetails.getUser());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-bookings")
    public ResponseEntity<List<MyBookingResponse>> getMyBookings(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        UserIn currentUser = userDetails.getUser();

        List<MyBookingResponse> bookings =
                bookingService.getMyBookings(currentUser.getId());

        return ResponseEntity.ok(bookings);
    }
}