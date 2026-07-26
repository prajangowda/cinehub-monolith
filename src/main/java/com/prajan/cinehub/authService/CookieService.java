package com.prajan.cinehub.authService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class CookieService {

    public void addAccessTokenCookie(
            HttpServletResponse response,
            String token) {

        ResponseCookie cookie =
                ResponseCookie.from("accessToken", token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Lax")
                        .path("/")
                        .maxAge(Duration.ofMinutes(15))
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString());
    }

    public void addRefreshTokenCookie(
            HttpServletResponse response,
            String token) {

        ResponseCookie cookie =
                ResponseCookie.from("refreshToken", token)
                        .httpOnly(true)
                        .secure(true)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(Duration.ofDays(7))
                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString());
    }

    public String extractTokenFromCookies(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("accessToken".equals(cookie.getName())) {

                return cookie.getValue();
            }
        }

        return null;
    }
}