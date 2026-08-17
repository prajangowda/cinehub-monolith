package com.prajan.cinehub.theatre.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTheatreRequest {

    private String name;

    private String address;

    private String city;

    private String state;

    @Pattern(regexp = "\\d{6}")
    private String pincode;

    private Double latitude;

    private Double longitude;

    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String phone;

    @Email
    private String email;

    private Boolean active;
}