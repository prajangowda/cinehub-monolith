package com.prajan.cinehub.auth.authService;

import com.prajan.cinehub.auth.dto.LoginResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Component
public class Oauth2SuccessHandle implements AuthenticationSuccessHandler {

    private final OauthService oauthService;
    private final CookieService cookieService;

    private  final ObjectMapper objectMapper;

    public Oauth2SuccessHandle(OauthService oauthService, CookieService cookieService, ObjectMapper objectMapper) {
        this.oauthService = oauthService;
        this.cookieService = cookieService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token= (OAuth2AuthenticationToken) authentication;
        OAuth2User oauth2User=(OAuth2User) authentication.getPrincipal();

       String registrationId=token.getAuthorizedClientRegistrationId();

        LoginResponse result = oauthService.handleOauthLoginRequest(oauth2User, registrationId);
        String redirectUrl = "http://localhost:5173/oauth-success";

        cookieService.addAccessTokenCookie(response, result.getAccessToken());

        cookieService.addRefreshTokenCookie(response,result.getRefreshToken());

        response.sendRedirect(redirectUrl);
    }
}
