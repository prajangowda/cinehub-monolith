package com.prajan.cinehub.booking.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.booking.dto.CreateBookingRequest;
import com.prajan.cinehub.booking.dto.MyBookingResponse;
import com.prajan.cinehub.booking.dto.ReservationResponse;
import com.prajan.cinehub.booking.dto.SeatReservation;
import com.prajan.cinehub.booking.entity.Booking;
import com.prajan.cinehub.booking.repository.BookingRepository;
import com.prajan.cinehub.show.entity.Show;
import com.prajan.cinehub.show.entity.ShowSeat;
import com.prajan.cinehub.show.enums.SeatStatus;
import com.prajan.cinehub.show.repository.ShowRepository;
import com.prajan.cinehub.show.repository.ShowSeatRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final BigDecimal SEAT_PRICE =
            BigDecimal.valueOf(200);

    private static final Duration RESERVATION_DURATION =
            Duration.ofMinutes(10);


// ============================================================
// RESERVE SEATS
// ============================================================

    public ReservationResponse reserveSeats(
            CreateBookingRequest request,
            UserIn user
    ) {

        log.info(
                "Starting seat reservation | showId={} | seatIds={} | userId={}",
                request.showId(),
                request.showSeatIds(),
                user.getId()
        );


        // 1. Find show
        Show show = showRepository.findById(request.showId())
                .orElseThrow(() -> {

                    log.warn(
                            "Seat reservation failed | Show not found | showId={}",
                            request.showId()
                    );

                    return new RuntimeException("Show not found");
                });


        log.info(
                "Show found | showId={}",
                show.getId()
        );


        // 2. Get selected show seats
        List<ShowSeat> showSeats =
                showSeatRepository.findByIdIn(
                        request.showSeatIds()
                );


        log.info(
                "Requested seats={} | Found seats={}",
                request.showSeatIds().size(),
                showSeats.size()
        );


        // 3. Validate all requested seats exist
        if (showSeats.size() != request.showSeatIds().size()) {

            log.warn(
                    "Seat reservation failed | One or more seats do not exist | requested={} | found={}",
                    request.showSeatIds(),
                    showSeats.stream()
                            .map(ShowSeat::getId)
                            .toList()
            );

            throw new RuntimeException(
                    "One or more seats do not exist"
            );
        }


        // 4. Validate seats belong to this show
        boolean invalidShowSeat = showSeats.stream()
                .anyMatch(showSeat ->
                        !showSeat.getShow()
                                .getId()
                                .equals(show.getId())
                );

        if (invalidShowSeat) {

            log.warn(
                    "Seat reservation failed | Seat does not belong to show | showId={} | seatIds={}",
                    show.getId(),
                    request.showSeatIds()
            );

            throw new RuntimeException(
                    "One or more seats do not belong to this show"
            );
        }


        // 5. Check PostgreSQL availability
        boolean seatNotAvailable = showSeats.stream()
                .anyMatch(showSeat ->
                        showSeat.getStatus()
                                != SeatStatus.AVAILABLE
                );

        if (seatNotAvailable) {

            log.warn(
                    "Seat reservation failed | PostgreSQL seat unavailable | showId={} | seatIds={}",
                    show.getId(),
                    request.showSeatIds()
            );

            throw new RuntimeException(
                    "One or more seats are not available"
            );
        }


        // 6. Calculate total amount on backend
        BigDecimal totalAmount =
                SEAT_PRICE.multiply(
                        BigDecimal.valueOf(showSeats.size())
                );


        log.info(
                "Reservation amount calculated | showId={} | seatCount={} | totalAmount={}",
                show.getId(),
                showSeats.size(),
                totalAmount
        );


        // 7. Generate unique reservation token
        String reservationToken =
                UUID.randomUUID().toString();


        log.info(
                "Reservation token generated | token={}",
                reservationToken
        );


        // Keep track of successfully locked seats
        List<String> lockedKeys = new ArrayList<>();


        try {

            // 8. Try locking every seat in Redis
            for (ShowSeat showSeat : showSeats) {

                String redisKey =
                        "cinehub:seat-lock:show:"
                                + show.getId()
                                + ":seat:"
                                + showSeat.getId();


                log.info(
                        "Attempting Redis seat lock | key={} | token={}",
                        redisKey,
                        reservationToken
                );


                Boolean locked =
                        redisTemplate.opsForValue().setIfAbsent(
                                redisKey,
                                reservationToken,
                                RESERVATION_DURATION
                        );


                log.info(
                        "Redis seat lock result | key={} | locked={} | ttl={} seconds",
                        redisKey,
                        locked,
                        redisTemplate.getExpire(
                                redisKey,
                                TimeUnit.SECONDS
                        )
                );


                // Seat already locked by another user
                if (!Boolean.TRUE.equals(locked)) {

                    log.warn(
                            "Seat already locked | key={} | requestedToken={}",
                            redisKey,
                            reservationToken
                    );

                    throw new RuntimeException(
                            "One or more selected seats were just reserved by another user"
                    );
                }


                lockedKeys.add(redisKey);
            }


            log.info(
                    "All requested seats successfully locked in Redis | token={} | seatCount={}",
                    reservationToken,
                    showSeats.size()
            );


            // 9. Create temporary reservation object
            SeatReservation reservation =
                    new SeatReservation(
                            reservationToken,
                            show.getId(),
                            showSeats.stream()
                                    .map(ShowSeat::getId)
                                    .toList(),
                            user.getId(),
                            totalAmount
                    );


            // 10. Store reservation details in Redis
            String reservationKey =
                    "cinehub:reservation:" + reservationToken;


            log.info(
                    "Creating Redis reservation | key={} | token={}",
                    reservationKey,
                    reservationToken
            );


            String reservationJson =
                    objectMapper.writeValueAsString(reservation);


            redisTemplate.opsForValue().set(
                    reservationKey,
                    reservationJson,
                    RESERVATION_DURATION
            );


            Long reservationTtl =
                    redisTemplate.getExpire(
                            reservationKey,
                            TimeUnit.SECONDS
                    );


            log.info(
                    "Redis reservation created successfully | key={} | ttl={} seconds | token={}",
                    reservationKey,
                    reservationTtl,
                    reservationToken
            );


            // Verify immediately after writing
            String storedReservation =
                    redisTemplate.opsForValue()
                            .get(reservationKey);


            log.info(
                    "Redis reservation verification | key={} | exists={} | tokenMatches={}",
                    reservationKey,
                    storedReservation != null,
                    storedReservation != null &&
                            storedReservation.contains(reservationToken)
            );


        } catch (Exception e) {

            log.error(
                    "Seat reservation failed | token={} | error={}",
                    reservationToken,
                    e.getMessage(),
                    e
            );


            // Release only the locks created by THIS request
            for (String key : lockedKeys) {

                String lockToken =
                        redisTemplate.opsForValue().get(key);


                log.info(
                        "Rollback Redis seat lock | key={} | storedTokenMatches={}",
                        key,
                        reservationToken.equals(lockToken)
                );


                if (reservationToken.equals(lockToken)) {

                    redisTemplate.delete(key);

                    log.info(
                            "Redis seat lock deleted during rollback | key={}",
                            key
                    );
                }
            }


            throw new RuntimeException(
                    "Unable to reserve selected seats",
                    e
            );
        }


        // 11. Return reservation information to frontend
        log.info(
                "Seat reservation completed successfully | token={} | amount={} | expiresIn={} seconds",
                reservationToken,
                totalAmount,
                RESERVATION_DURATION.getSeconds()
        );


        return new ReservationResponse(
                reservationToken,
                totalAmount,
                RESERVATION_DURATION.getSeconds()
        );
    }


// ============================================================
// MY BOOKINGS
// ============================================================

    public List<MyBookingResponse> getMyBookings(Long userId) {

        log.info(
                "Fetching bookings | userId={}",
                userId
        );


        List<Booking> bookings =
                bookingRepository.findByUserIdOrderByBookedAtDesc(userId);


        log.info(
                "Bookings found | userId={} | count={}",
                userId,
                bookings.size()
        );


        return bookings.stream()
                .map(booking -> {

                    List<String> seats =
                            booking.getBookingSeats()
                                    .stream()
                                    .map(bookingSeat -> {

                                        ShowSeat showSeat =
                                                bookingSeat.getShowSeat();

                                        return showSeat.getSeat().getRowName()
                                                + showSeat.getSeat().getSeatNumber();
                                    })
                                    .toList();


                    return MyBookingResponse.builder()
                            .bookingId(booking.getId())
                            .bookingReference(
                                    booking.getBookingReference()
                            )
                            .status(
                                    booking.getStatus().name()
                            )
                            .totalAmount(
                                    booking.getTotalAmount()
                            )
                            .showDate(
                                    booking.getShow().getShowDate()
                            )
                            .startTime(
                                    booking.getShow().getStartTime()
                            )
                            .movieTitle(
                                    booking.getShow()
                                            .getMovie()
                                            .getTitle()
                            )
                            .seats(seats)
                            .build();
                })
                .toList();
    }


}
