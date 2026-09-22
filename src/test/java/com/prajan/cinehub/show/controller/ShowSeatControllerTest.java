package com.prajan.cinehub.show.controller;

import com.prajan.cinehub.show.dto.ShowSeatResponse;
import com.prajan.cinehub.show.enums.SeatStatus;
import com.prajan.cinehub.show.service.ShowSeatService;
import com.prajan.cinehub.theatre.enums.SeatType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowSeatControllerTest {

    @Mock
    private ShowSeatService showSeatService;

    @InjectMocks
    private ShowController showSeatController;


    @Test
    void getShowSeats_shouldReturnSeatsForShow() {

        // Arrange
        Long showId = 1L;

        ShowSeatResponse seat1 = ShowSeatResponse.builder()
                .id(1L)
                .seatId(101L)
                .rowName("A")
                .seatNumber(1)
                .seatType(SeatType.REGULAR)
                .status(SeatStatus.AVAILABLE)
                .build();

        ShowSeatResponse seat2 = ShowSeatResponse.builder()
                .id(2L)
                .seatId(102L)
                .rowName("A")
                .seatNumber(2)
                .seatType(SeatType.REGULAR)
                .status(SeatStatus.BOOKED)
                .build();

        List<ShowSeatResponse> expectedSeats =
                List.of(seat1, seat2);

        when(showSeatService.getSeatsByShow(showId))
                .thenReturn(expectedSeats);


        // Act
        List<ShowSeatResponse> actualSeats =
                showSeatController.getShowSeats(showId);


        // Assert
        assertNotNull(actualSeats);

        assertEquals(2, actualSeats.size());

        assertEquals(1L, actualSeats.get(0).getId());
        assertEquals("A", actualSeats.get(0).getRowName());
        assertEquals(1, actualSeats.get(0).getSeatNumber());
        assertEquals(SeatStatus.AVAILABLE,
                actualSeats.get(0).getStatus());

        assertEquals(SeatStatus.BOOKED,
                actualSeats.get(1).getStatus());


        // Verify
        verify(showSeatService, times(1))
                .getSeatsByShow(showId);
    }
}