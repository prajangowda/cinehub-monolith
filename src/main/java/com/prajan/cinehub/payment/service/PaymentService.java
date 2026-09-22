package com.prajan.cinehub.payment.service;

import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.UserInRepository;
import com.prajan.cinehub.booking.entity.Booking;
import com.prajan.cinehub.booking.entity.BookingSeat;
import com.prajan.cinehub.booking.enums.BookingStatus;
import com.prajan.cinehub.booking.repository.BookingRepository;
import com.prajan.cinehub.payment.dto.PaymentVerificationRequest;
import com.prajan.cinehub.show.entity.Show;
import com.prajan.cinehub.show.entity.ShowSeat;
import com.prajan.cinehub.show.enums.SeatStatus;
import com.prajan.cinehub.show.repository.ShowRepository;
import com.prajan.cinehub.show.repository.ShowSeatRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prajan.cinehub.booking.dto.SeatReservation;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BookingRepository bookingRepository;
    private final  StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final UserInRepository userRepository;
    private final ShowRepository showRepository;
    private final ShowSeatRepository showSeatRepository;

    // CREATE RAZORPAY ORDER
    @Transactional
    public Map<String, Object> createOrder(
            String reservationToken
    ) throws Exception {

        // 1. Get reservation from Redis
        String reservationKey =
                "cinehub:reservation:" + reservationToken;

        String reservationJson =
                redisTemplate.opsForValue().get(reservationKey);

        if (reservationJson == null) {
            throw new RuntimeException(
                    "Seat reservation has expired. Please select your seats again."
            );
        }


        // 2. Convert Redis JSON to reservation object
        SeatReservation reservation =
                objectMapper.readValue(
                        reservationJson,
                        SeatReservation.class
                );


        // 3. Get user
        UserIn user = userRepository
                .findById(reservation.getUserId())
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );


        // 4. Get show
        Show show = showRepository
                .findById(reservation.getShowId())
                .orElseThrow(() ->
                        new RuntimeException("Show not found")
                );


        // 5. Get selected seats
        List<ShowSeat> showSeats =
                showSeatRepository.findByIdIn(
                        reservation.getShowSeatIds()
                );


        if (showSeats.size()
                != reservation.getShowSeatIds().size()) {

            throw new RuntimeException(
                    "One or more seats not found"
            );
        }


        // 6. Create pending booking
        Booking booking = Booking.builder()
                .bookingReference(
                        "CINE-" +
                                UUID.randomUUID()
                                        .toString()
                                        .substring(0, 8)
                                        .toUpperCase()
                )
                .user(user)
                .show(show)
                .status(BookingStatus.PAYMENT_PENDING)
                .reservationToken(reservationToken)
                .totalAmount(reservation.getTotalAmount())
                .bookingSeats(new ArrayList<>())
                .build();


        // 7. Create BookingSeat entries
        for (ShowSeat showSeat : showSeats) {

            BookingSeat bookingSeat =
                    BookingSeat.builder()
                            .booking(booking)
                            .showSeat(showSeat)
                            .price(BigDecimal.valueOf(200))
                            .build();

            booking.getBookingSeats().add(bookingSeat);
        }


        // 8. Save booking and booking seats
        bookingRepository.save(booking);


        // 9. Create Razorpay order
        int amount =
                reservation.getTotalAmount().intValue();

        RazorpayClient razorpayClient =
                new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest =
                new JSONObject();

        orderRequest.put(
                "amount",
                amount * 100
        );

        orderRequest.put(
                "currency",
                "INR"
        );

        orderRequest.put(
                "receipt",
                booking.getBookingReference()
        );


        Order order =
                razorpayClient.orders.create(orderRequest);


        // 10. Return everything frontend needs
        Map<String, Object> response =
                new HashMap<>();

        response.put("id", order.get("id"));
        response.put("amount", order.get("amount"));
        response.put("currency", order.get("currency"));

        response.put(
                "bookingId",
                booking.getId()
        );

        return response;
    }

    // VERIFY PAYMENT
    @Transactional
    public Map<String, Object> verifyPayment(
            PaymentVerificationRequest request
    ) throws Exception {

        // 1. Find booking
        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() ->
                        new RuntimeException("Booking not found")
                );


        // 2. Prevent duplicate verification
        if (booking.getStatus() == BookingStatus.CONFIRMED) {

            return Map.of(
                    "success", true,
                    "message", "Booking already confirmed",
                    "bookingId", booking.getId()
            );
        }


        // 3. Verify Razorpay signature
        JSONObject options = new JSONObject();

        options.put(
                "razorpay_order_id",
                request.getRazorpayOrderId()
        );

        options.put(
                "razorpay_payment_id",
                request.getRazorpayPaymentId()
        );

        options.put(
                "razorpay_signature",
                request.getRazorpaySignature()
        );


        boolean isValid = Utils.verifyPaymentSignature(
                options,
                keySecret
        );


        // 4. Payment verification failed
        if (!isValid) {

            booking.setStatus(BookingStatus.PAYMENT_FAILED);

            bookingRepository.save(booking);

            return Map.of(
                    "success", false,
                    "message", "Payment verification failed"
            );
        }


        // 5. Mark all seats as BOOKED
        booking.getBookingSeats()
                .forEach(bookingSeat -> {

                    ShowSeat showSeat =
                            bookingSeat.getShowSeat();

                    showSeat.setStatus(
                            SeatStatus.BOOKED
                    );

                    // Delete Redis seat lock
                    String redisKey =
                            "cinehub:seat-lock:show:"
                                    + booking.getShow().getId()
                                    + ":seat:"
                                    + showSeat.getId();

                    redisTemplate.delete(redisKey);
                });


        // 6. Confirm booking
        booking.setStatus(
                BookingStatus.CONFIRMED
        );

        bookingRepository.save(booking);


        return Map.of(
                "success", true,
                "message", "Payment verified successfully",
                "bookingId", booking.getId()
        );
    }


}