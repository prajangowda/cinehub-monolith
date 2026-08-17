package com.prajan.cinehub.auth.TheatreOwnerRequest;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
public class TheatreOwnerResponseDto {
    private Long id;

    private String businessName;

    private String phone;

    private String address;

    private String gstNumber;

    private RequestStatus status;

    private LocalDateTime requestedAt;
}
