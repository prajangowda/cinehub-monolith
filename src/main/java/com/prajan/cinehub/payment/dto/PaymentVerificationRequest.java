package com.prajan.cinehub.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerificationRequest {

    private String razorpayPaymentId;

    private String razorpayOrderId;

    private String razorpaySignature;

    private Long bookingId;
}