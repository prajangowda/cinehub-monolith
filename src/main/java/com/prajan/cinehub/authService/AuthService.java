package com.prajan.cinehub.authService;


import com.prajan.cinehub.dto.LoginRequest;
import com.prajan.cinehub.dto.LoginResponse;
import com.prajan.cinehub.dto.SingupRequest;
import com.prajan.cinehub.enums.Role;
import com.prajan.cinehub.enums.provider;
import com.prajan.cinehub.model.CustomUserDetails;
import com.prajan.cinehub.model.UserIn;
import com.prajan.cinehub.repository.UserInRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Service
public class AuthService {

    @Autowired
    private UserInRepository userRepo;



    @Autowired
    private PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public AuthService(AuthenticationManager authenticationManager, HandlerExceptionResolver handlerExceptionResolver) {
        this.authenticationManager = authenticationManager;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Autowired
    private JWTservice jwtservice;

    @Autowired
    private UserInRepository userInRepository;

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


        String accessToken = jwtservice.generateAccessToken(userPrincipal);

//        String refreshToken =
//                jwtService.generateRefreshToken(user);

        return new LoginResponse(accessToken);

    }

    @Transactional
    public String signup(SingupRequest signupdto) {

        UserIn user = userRepo.findByEmail(signupdto.getEmail()).orElse(null);

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

        userRepo.save(user);

        return "User created";
    }


}
