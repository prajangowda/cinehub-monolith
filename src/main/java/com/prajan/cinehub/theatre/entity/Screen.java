package com.prajan.cinehub.theatre.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.prajan.cinehub.theatre.enums.ScreenType;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Entity
@Table(name = "screens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ScreenType type;

    private Integer rows;

    private Integer seatsPerRow;

    private Integer totalSeats;

    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theatre_id")
    @JsonIgnore
    private Theatre theatre;

    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL)
    private List<Seat> seats;
}