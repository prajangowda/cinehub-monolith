package com.prajan.cinehub.auth.controller;


import com.prajan.cinehub.auth.authService.AuthService;
import com.prajan.cinehub.auth.authService.CookieService;
import com.prajan.cinehub.auth.dto.*;
import com.prajan.cinehub.auth.enums.SignupResponse;
import com.prajan.cinehub.auth.repository.UserInRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {


    final private UserInRepository userrepo;

    final private AuthService authService;

    final private CookieService cookieService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto, HttpServletResponse response) {

        LoginResponse result= authService.login(dto);

        cookieService.addAccessTokenCookie(response, result.getAccessToken());

        cookieService.addRefreshTokenCookie(response,result.getRefreshToken());

        return ResponseEntity.ok("Logged In");
    }

    //signUp
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Valid @RequestBody SingupRequest request) {

        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request,HttpServletResponse response) {
        LoginResponse result = authService.verifyOtp(request);

        cookieService.addAccessTokenCookie(response, result.getAccessToken());

        cookieService.addRefreshTokenCookie(response,result.getRefreshToken());

        return ResponseEntity.ok("Logged In");
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(
            @RequestParam String email) {

        return ResponseEntity.ok(authService.resendOtp(email));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        cookieService.logout(response);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        LoginResponse result=authService.refreshToken(request, response);
        cookieService.addAccessTokenCookie(response, result.getAccessToken());
        return ResponseEntity.ok("access token issed");
    }
}
