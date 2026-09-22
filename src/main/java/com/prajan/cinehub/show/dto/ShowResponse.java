package com.prajan.cinehub.show.dto;

import com.prajan.cinehub.show.enums.ShowStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowResponse {

    private Long id;

    private Long movieId;
    private String movieTitle;

    private Long screenId;
    private String screenName;

    private Long theatreId;
    private String theatreName;

    private LocalDate showDate;

    private LocalTime startTime;
    private LocalTime endTime;

    private ShowStatus status;

    private Boolean active;
}