package com.prajan.cinehub.auth.authService;

import com.prajan.cinehub.auth.dto.LoginRequest;
import com.prajan.cinehub.auth.dto.LoginResponse;
import com.prajan.cinehub.auth.dto.SingupRequest;
import com.prajan.cinehub.auth.dto.UserResponse;
import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.enums.provider;
import com.prajan.cinehub.auth.model.CustomUserDetails;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.UserInRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserInRepository userInRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final CookieService cookieService;
    private final JWTservice jwtService;
    private final UserDetailsService userDetailsService;

    public LoginResponse login(LoginRequest LoginDto)
    {

        Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            LoginDto.getEmail(),
                            LoginDto.getPassword()
                    )
            );

        CustomUserDetails userPrincipal = (CustomUserDetails) authentication.getPrincipal();

        if (!userPrincipal.isEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Account disabled by admin"
            );
        }


        String accessToken = jwtService.generateAccessToken(userPrincipal);

        String refreshToken = jwtService.generateRefreshToken(userPrincipal);


        return new LoginResponse(accessToken,refreshToken);

    }

    @Transactional
    public String signup(SingupRequest signupdto) {

        UserIn user =userInRepository.findByEmail(signupdto.getEmail()).orElse(null);

        if (user != null) {
            throw new RuntimeException("User already exists");
        }

        user = UserIn.builder()
                .email(signupdto.getEmail())
                .password(passwordEncoder.encode(signupdto.getPassword()))
                .provider(provider.EMAIL)
                .role(Role.USER)
                .active(true)
                .build();

        userInRepository.save(user);

        return "User created";
    }

    public LoginResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieService.extractTokenFromCookies(request, "refreshToken");

        if (refreshToken == null || !jwtService.isValid(refreshToken)) {
            throw new BadCredentialsException("Invalid Refresh Token");
        }

        String username = jwtService.getEmailFromToken(refreshToken);

       CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponse(accessToken);
    }

    public UserResponse getCurrentUser(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not authenticated"
            );
        }

        String email = authentication.getName();

        UserIn user = userInRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }


}
