package com.prajan.cinehub.controller;


import com.prajan.cinehub.authService.AuthService;
import com.prajan.cinehub.dto.LoginRequest;
import com.prajan.cinehub.dto.LoginResponse;
import com.prajan.cinehub.dto.SingupRequest;
import com.prajan.cinehub.repository.UserInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserInRepository userrepo;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest dto) {
        LoginResponse response = authService.login(dto);
        return ResponseEntity.ok(response);
    }

    //signUp
    @PostMapping("/signup")
    public String signUpDonor(@RequestBody SingupRequest signupdto) {
        return  authService.signup(signupdto);
    }


}
