package com.prajan.cinehub.auth.TheatreOwnerRequest;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TheatreOwnerRequestDto {
    @NotBlank
    private String businessName;

    @NotBlank
    private String phone;

    @NotBlank
    private String address;

    private String gstNumber;
}
