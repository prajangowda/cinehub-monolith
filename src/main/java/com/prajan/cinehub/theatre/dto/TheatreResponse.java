package com.prajan.cinehub.theatre.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TheatreResponse {

    private Long id;

    private String name;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double latitude;

    private Double longitude;

    private String phone;

    private String email;

    private Boolean active;
}