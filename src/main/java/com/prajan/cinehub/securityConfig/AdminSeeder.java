package com.prajan.cinehub.securityConfig;

import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.UserInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {


    private final UserInRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.findByEmail("admin@cinehub.com").isEmpty()) {

            UserIn admin = UserIn.builder()
                    .name("Admin")
                    .email("admin@cinehub.com")
                    .password(passwordEncoder.encode("Admin@123"))
                    .role(Role.ADMIN)
                    .active(true)
                    .build();

            userRepository.save(admin);
        }
    }
}