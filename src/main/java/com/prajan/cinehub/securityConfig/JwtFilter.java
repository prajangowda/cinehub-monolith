package com.prajan.cinehub.securityConfig;

import com.prajan.cinehub.auth.authService.CookieService;
import com.prajan.cinehub.auth.authService.JWTservice;
import com.prajan.cinehub.auth.model.CustomUserDetails;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.UserInRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JWTservice jwtService;
    private final UserInRepository repo;
    private final CookieService cookieService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String servletPath = request.getServletPath();

        log.info("========== JWT FILTER START ==========");
        log.info("Request Method   : {}", method);
        log.info("Request URI      : {}", uri);
        log.info("Servlet Path     : {}", servletPath);

        try {

            String token = cookieService.extractTokenFromCookies(
                    request,
                    "accessToken"
            );

            log.info(
                    "Access token present: {}",
                    token != null
            );

            /*
             * No JWT cookie.
             * Continue the request and let Spring Security
             * decide whether the endpoint is public or protected.
             */
            if (token == null) {

                log.info(
                        "No access token found. Continuing filter chain."
                );

                filterChain.doFilter(request, response);

                log.info(
                        "========== JWT FILTER END =========="
                );

                return;
            }

            log.info("Access token found. Validating JWT...");

            String email = jwtService.getEmailFromToken(token);

            log.info(
                    "JWT validation completed. Email extracted: {}",
                    email != null ? email : "null"
            );

            if (email != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                log.info(
                        "Searching database for user: {}",
                        email
                );

                UserIn user = repo.findByEmail(email)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "User not found"
                                )
                        );

                log.info(
                        "User found. Active: {}, Role: {}",
                        user.isActive(),
                        user.getRole()
                );

                if (!user.isActive()) {

                    log.warn(
                            "User account is disabled: {}",
                            email
                    );

                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Account disabled by admin"
                    );
                }

                CustomUserDetails userPrincipal =
                        new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userPrincipal,
                                null,
                                userPrincipal.getAuthorities()
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

                log.info(
                        "Authentication set successfully for user: {}",
                        email
                );

                log.info(
                        "Authorities: {}",
                        userPrincipal.getAuthorities()
                );

            } else if (email == null) {

                log.warn(
                        "JWT did not contain a valid email."
                );

            } else {

                log.info(
                        "SecurityContext already contains authentication."
                );
            }

            log.info(
                    "Continuing filter chain for: {} {}",
                    method,
                    uri
            );

            filterChain.doFilter(request, response);

            log.info(
                    "Response status: {}",
                    response.getStatus()
            );

        } catch (JwtException ex) {

            log.error(
                    "JWT validation failed for {} {}: {}",
                    method,
                    uri,
                    ex.getMessage()
            );

            SecurityContextHolder.clearContext();

            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    ex
            );

            return;

        } catch (UsernameNotFoundException ex) {

            log.error(
                    "User not found while processing {} {}: {}",
                    method,
                    uri,
                    ex.getMessage()
            );

            SecurityContextHolder.clearContext();

            handlerExceptionResolver.resolveException(
                    request,
                    response,
                    null,
                    ex
            );

            return;

        } finally {

            log.info(
                    "========== JWT FILTER END =========="
            );
        }
    }
}