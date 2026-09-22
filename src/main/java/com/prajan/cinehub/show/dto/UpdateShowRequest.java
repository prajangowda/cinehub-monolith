package com.prajan.cinehub.show.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateShowRequest {

    private LocalDate showDate;

    private LocalTime startTime;

    private LocalTime endTime;
}