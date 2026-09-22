package com.prajan.cinehub.payment.controller;

import com.prajan.cinehub.payment.dto.CreateOrderRequest;
import com.prajan.cinehub.payment.dto.PaymentVerificationRequest;
import com.prajan.cinehub.payment.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

   // CREATE RAZORPAY ORDER
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(
            @RequestBody CreateOrderRequest request
    ) throws Exception {

        return ResponseEntity.ok(
                paymentService.createOrder(
                        request.reservationToken()
                )
        );
    }

    // VERIFY RAZORPAY PAYMENT
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(
            @RequestBody PaymentVerificationRequest request
    ) throws Exception {

        Map<String, Object> response =
                paymentService.verifyPayment(request);

        return ResponseEntity.ok(response);
    }
}