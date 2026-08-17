package com.prajan.cinehub.theatre.mapper;


import com.prajan.cinehub.theatre.dto.ScreenResponse;
import com.prajan.cinehub.theatre.entity.Screen;
import org.springframework.context.annotation.Bean;


public class ScreenMapper {

    private ScreenMapper() {
    }

    public static ScreenResponse toResponse(Screen screen) {

        return new ScreenResponse(
                screen.getId(),
                screen.getName(),
                screen.getType(),
                screen.getRows(),
                screen.getSeatsPerRow(),
                screen.getTotalSeats(),
                screen.getActive(),
                screen.getTheatre().getId()
        );
    }
}
