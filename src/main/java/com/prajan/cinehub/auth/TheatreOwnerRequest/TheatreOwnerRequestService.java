package com.prajan.cinehub.auth.TheatreOwnerRequest;

import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.auth.repository.UserInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class TheatreOwnerRequestService {
    private final UserInRepository userRepository;
    private final TheatreOwnerRequestRepository requestRepository;


    public TheatreOwnerResponseDto submitRequest(
            Long userId,
            TheatreOwnerRequest dto
    ) {

        UserIn user = userRepository.findById(userId)
                .orElseThrow();

        requestRepository.findByUserAndStatus(user, RequestStatus.PENDING)
                .ifPresent(r -> {
                    throw new RuntimeException(
                            "Pending request already exists");
                });

        TheatreOwnerRequest request = TheatreOwnerRequest.builder()
                .user(user)
                .businessName(dto.getBusinessName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .gstNumber(dto.getGstNumber())
                .status(RequestStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        requestRepository.save(request);

        return TheatreOwnerResponseDto.builder()
                .id(request.getId())
                .businessName(request.getBusinessName())
                .phone(request.getPhone())
                .address(request.getAddress())
                .gstNumber(request.getGstNumber())
                .status(request.getStatus())
                .requestedAt(request.getRequestedAt())
                .build();
    }


    public List<TheatreOwnerResponseDto> getPendingRequests() {

        return requestRepository.findByStatus(RequestStatus.PENDING)
                .stream()
                .map(r -> TheatreOwnerResponseDto.builder()
                        .id(r.getId())
                        .businessName(r.getBusinessName())
                        .phone(r.getPhone())
                        .address(r.getAddress())
                        .gstNumber(r.getGstNumber())
                        .status(r.getStatus())
                        .requestedAt(r.getRequestedAt())
                        .build())
                .toList();
    }


    public void approve(Long requestId) {

        TheatreOwnerRequest request =
                requestRepository.findById(requestId)
                        .orElseThrow();

        request.setStatus(RequestStatus.APPROVED);

        UserIn user = userRepository.findById(request.getUser().getId())
                .orElseThrow();
        user.setRole(Role.THEATRE_OWNER);

        userRepository.save(user);
        requestRepository.save(request);
    }


    public void reject(Long requestId) {

        TheatreOwnerRequest request =
                requestRepository.findById(requestId)
                        .orElseThrow();

        request.setStatus(RequestStatus.REJECTED);

        requestRepository.save(request);
    }
}
