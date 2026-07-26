package com.prajan.cinehub.securityConfig;

import com.prajan.cinehub.authService.CookieService;
import com.prajan.cinehub.authService.JWTservice;
import com.prajan.cinehub.model.CustomUserDetails;
import com.prajan.cinehub.model.UserIn;
import com.prajan.cinehub.repository.UserInRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private  final CookieService cookieService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            String token = cookieService.extractTokenFromCookies(request);

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }

            String email = jwtService.getEmailFromToken(token);


            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserIn user = repo.findByEmail(email).orElse(null);

                if (!user.isActive())
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Account disabled by admin"
                    );

                CustomUserDetails userPrincipal = new CustomUserDetails(user);

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
            filterChain.doFilter(request, response);
        }catch(Exception ex)
        {
           handlerExceptionResolver.resolveException(request,response,null,ex);
        }
    }
}
