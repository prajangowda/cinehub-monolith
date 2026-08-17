package com.prajan.cinehub.theatre.dto;


import com.prajan.cinehub.theatre.enums.ScreenType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScreenResponse {

    private Long id;

    private String name;

    private ScreenType type;

    private Integer rows;

    private Integer seatsPerRow;

    private Integer totalSeats;

    private Boolean active;

    private Long theatreId;
}