package com.prajan.cinehub.securityConfig;

import com.prajan.cinehub.authService.JWTservice;
import com.prajan.cinehub.model.CustomUserDetails;
import com.prajan.cinehub.model.UserIn;
import com.prajan.cinehub.repository.UserInRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JWTservice jwtservice;
    private final UserInRepository repo;

    public JwtFilter(HandlerExceptionResolver handlerExceptionResolver, JWTservice jwtservice, UserInRepository repo) {
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtservice = jwtservice;
        this.repo = repo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String requestToken = request.getHeader("Authorization");
            if (requestToken == null || !requestToken.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = requestToken.split("Bearer ")[1];
            String email= jwtservice.getemailfromtoken(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserIn user = repo.findByEmail(email).orElse(null);

                if (!user.isActive()) {
                    throw new ResponseStatusException(
                            HttpStatus.FORBIDDEN,
                            "Account disabled by admin"
                    );
                }
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
