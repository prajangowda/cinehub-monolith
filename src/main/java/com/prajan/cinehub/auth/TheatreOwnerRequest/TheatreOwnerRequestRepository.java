package com.prajan.cinehub.auth.TheatreOwnerRequest;

import com.prajan.cinehub.auth.model.UserIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TheatreOwnerRequestRepository extends JpaRepository<TheatreOwnerRequest, Long> {

    Optional<TheatreOwnerRequest> findByUserAndStatus(
            UserIn user,
            RequestStatus status
    );

    List<TheatreOwnerRequest> findByStatus(RequestStatus status);
}