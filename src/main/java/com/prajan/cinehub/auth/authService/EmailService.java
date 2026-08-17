package com.prajan.cinehub.auth.authService;


import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendOtp(String to, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject("CineHub Email Verification");

        message.setText("""
                Welcome to CineHub!

                Your OTP is:

                %s

                This OTP is valid for 5 minutes.

                If you didn't request this verification, please ignore this email.

                Regards,
                CineHub Team
                """.formatted(otp));

        mailSender.send(message);
    }
}
