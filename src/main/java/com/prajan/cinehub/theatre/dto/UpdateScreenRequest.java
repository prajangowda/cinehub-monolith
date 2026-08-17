package com.prajan.cinehub.theatre.dto;


import com.prajan.cinehub.theatre.enums.ScreenType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateScreenRequest {

    private String name;

    private ScreenType type;

    private Boolean active;
}