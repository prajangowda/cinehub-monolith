package com.prajan.cinehub.auth.enums;

public record SignupResponse(
        boolean otpRequired,
        String message
) {}