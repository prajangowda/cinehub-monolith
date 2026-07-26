package com.prajan.cinehub.controller;


import com.prajan.cinehub.authService.AuthService;
import com.prajan.cinehub.authService.CookieService;
import com.prajan.cinehub.dto.LoginRequest;
import com.prajan.cinehub.dto.LoginResponse;
import com.prajan.cinehub.dto.SingupRequest;
import com.prajan.cinehub.repository.UserInRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {


    final private UserInRepository userrepo;

    final private AuthService authService;

    final private CookieService cookieService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto, HttpServletResponse response) {
        LoginResponse result = authService.login(dto);

        cookieService.addAccessTokenCookie(
                response,
                result.getAccessToken());

        return ResponseEntity.ok("logged in ");
    }

    //signUp
    @PostMapping("/signup")
    public String signUpDonor(@RequestBody SingupRequest signupdto) {
        return  authService.signup(signupdto);
    }


}
