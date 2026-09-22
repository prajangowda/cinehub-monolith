package com.prajan.cinehub.show.mapper;

import com.prajan.cinehub.show.dto.ShowResponse;
import com.prajan.cinehub.show.entity.Show;
import com.prajan.cinehub.theatre.entity.Screen;
import com.prajan.cinehub.theatre.entity.Theatre;

public class ShowMapper {

    public static ShowResponse toResponse(Show show) {

        Screen screen = show.getScreen();
        Theatre theatre = screen.getTheatre();

        return ShowResponse.builder()
                .id(show.getId())

                .movieId(show.getMovie().getId())
                .movieTitle(show.getMovie().getTitle())

                .screenId(screen.getId())
                .screenName(screen.getName())

                .theatreId(theatre.getId())
                .theatreName(theatre.getName())

                .showDate(show.getShowDate())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())

                .status(show.getStatus())
                .active(show.getActive())

                .build();
    }
}