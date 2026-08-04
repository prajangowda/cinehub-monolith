package com.prajan.cinehub.auth.controller;


import com.prajan.cinehub.auth.authService.AuthService;
import com.prajan.cinehub.auth.authService.CookieService;
import com.prajan.cinehub.auth.dto.LoginRequest;
import com.prajan.cinehub.auth.dto.LoginResponse;
import com.prajan.cinehub.auth.dto.SingupRequest;
import com.prajan.cinehub.auth.dto.UserResponse;
import com.prajan.cinehub.auth.repository.UserInRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
        LoginResponse result = authService.login(dto);

        cookieService.addAccessTokenCookie(response, result.getAccessToken());

        cookieService.addRefreshTokenCookie(response,result.getRefreshToken());

        return ResponseEntity.ok("Logged In");
    }

    //signUp
    @PostMapping("/signup")
    public String signUpDonor(@RequestBody SingupRequest signupdto) {
        return  authService.signup(signupdto);
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
