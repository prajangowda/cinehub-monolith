package com.prajan.cinehub.auth.authService;


import com.prajan.cinehub.auth.enums.provider;
import com.prajan.cinehub.auth.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTservice {


    @Value(value = "${jwt.secretKey}")
    private String jwtSecretKey;

    private SecretKey key()
    {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(CustomUserDetails userPrincipal)
    {
        String role = userPrincipal.getRole() != null
                ? userPrincipal.getRole().name()
                : "UNASSIGNED";   // or "NONE"

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())   // use setSubject (standard)
                .claim("role", role) // dynamic roles
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30)) // 10 min
                .signWith(key(), SignatureAlgorithm.HS256) // explicitly define algo
                .compact();


    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();

    }

    public boolean isValid(String token){

        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token);
            return true;
        }catch(Exception e){
            return false;
        }

    }

    public provider getProviderFromRegistrationId(String registrationId) {

        return switch (registrationId.toLowerCase()) {

            case "google" -> provider.GOOGLE;

//            case "github" -> provider.GITHUB;

            default -> throw new IllegalArgumentException(
                    "Unsupported registrationId: " + registrationId
            );
        };
    }

    public String generateRefreshToken(CustomUserDetails userPrincipal){

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(
                        System.currentTimeMillis()+7L*24*60*60*1000))
                .signWith(key())
                .compact();
    }


    public String extractProviderId(OAuth2User oauthUser, String registrationId) {

        String providerId;
        switch (registrationId.toLowerCase()) {
            case "google":
                providerId = oauthUser.getAttribute("sub");
                break;

            case "github":
                Object id = oauthUser.getAttribute("id");
                providerId = (id != null) ? id.toString() : null;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported registrationId: " + registrationId
                );
        }
        if (providerId == null || providerId.isBlank()) {
            throw new OAuth2AuthenticationException(
                    "Provider ID is missing from OAuth2 response"
            );
        }
        return providerId;
    }


}
