package com.prajan.cinehub.auth.repository;

import com.prajan.cinehub.auth.model.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, Long> {

    Optional<PendingRegistration> findByEmail(String email);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);

    void deleteAllByOtpExpiryBefore(LocalDateTime time);
}