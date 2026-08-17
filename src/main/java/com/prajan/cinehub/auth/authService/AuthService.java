package com.prajan.cinehub.auth.authService;

import com.prajan.cinehub.auth.dto.*;
import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.enums.SignupResponse;
import com.prajan.cinehub.auth.enums.provider;
import com.prajan.cinehub.auth.model.CustomUserDetails;
import com.prajan.cinehub.auth.model.PendingRegistration;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.PendingRegistrationRepository;
import com.prajan.cinehub.auth.repository.UserInRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserInRepository userInRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CookieService cookieService;
    private final JWTservice jwtService;
    private final UserDetailsService userDetailsService;
    private final PendingRegistrationRepository pendingRepository;
    private final EmailService emailService;

    public LoginResponse login(LoginRequest LoginDto)
    {

        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            LoginDto.getEmail(),
                            LoginDto.getPassword()
                    )
            );

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        if (!userPrincipal.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account disabled by admin"
            );
        }


        String accessToken = jwtService.generateAccessToken(userPrincipal);

        String refreshToken = jwtService.generateRefreshToken(userPrincipal);


        return new LoginResponse(accessToken,refreshToken);

    }

    @Transactional
    public SignupResponse signup(SingupRequest dto) {

        if (userInRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        pendingRepository.findByEmail(dto.getEmail())
                .ifPresent(pendingRepository::delete);

        String otp = String.format("%06d",
                new SecureRandom().nextInt(1_000_000));

        PendingRegistration pending =
                PendingRegistration.builder()
                        .name(dto.getName())
                        .email(dto.getEmail())
                        .password(passwordEncoder.encode(dto.getPassword()))
                        .otp(otp)
                        .otpExpiry(LocalDateTime.now().plusMinutes(5))
                        .createdAt(LocalDateTime.now())
                        .build();

        pendingRepository.save(pending);

        emailService.sendOtp(dto.getEmail(), otp);

        return new SignupResponse(true, "OTP sent successfully.");
    }

    // refresh token method
    public LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken");

        if (refreshToken == null || !jwtService.isValid(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        String username = jwtService.getEmailFromToken(refreshToken);

       CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(accessToken);
    }

    public UserResponse getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        String email = authentication.getName();

        UserIn user = userInRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }


    // Verify OTP and create user account
    @Transactional
    public LoginResponse verifyOtp(VerifyOtpRequest dto) {

        PendingRegistration pending =
                pendingRepository.findByEmail(dto.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException("OTP request not found"));

        if (LocalDateTime.now().isAfter(pending.getOtpExpiry())) {
            pendingRepository.delete(pending);
            throw new RuntimeException("OTP expired");
        }

        if (!pending.getOtp().equals(dto.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        UserIn user = new UserIn();

        user.setName(pending.getName());
        user.setEmail(pending.getEmail());
        user.setPassword(pending.getPassword());
        user.setRole(Role.USER);
        user.setActive(true);

        userInRepository.save(user);

        CustomUserDetails userPrincipal= new CustomUserDetails(user);

        pendingRepository.delete(pending);

        String accessToken = jwtService.generateAccessToken(userPrincipal);

        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void deleteExpiredPendingRegistrations() {
        pendingRepository.deleteAllByOtpExpiryBefore(LocalDateTime.now());
    }

    // Resend OTP
    @Transactional
    public String resendOtp(String email) {

        PendingRegistration pending = pendingRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("No pending registration found"));

        String otp = String.format("%06d",
                new SecureRandom().nextInt(1_000_000));

        pending.setOtp(otp);
        pending.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        pendingRepository.save(pending);

        emailService.sendOtp(email, otp);

        return "OTP sent successfully.";
    }
}
