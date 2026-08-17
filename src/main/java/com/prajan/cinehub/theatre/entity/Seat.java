package com.prajan.cinehub.theatre.entity;

import com.prajan.cinehub.theatre.enums.SeatType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "seats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rowName;

    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    private SeatType seatType;

    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    private Screen screen;
}