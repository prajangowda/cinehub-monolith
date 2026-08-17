package com.prajan.cinehub.theatre.service;



import com.prajan.cinehub.error.ResourceNotFoundException;
import com.prajan.cinehub.theatre.dto.CreateScreenRequest;
import com.prajan.cinehub.theatre.dto.ScreenResponse;
import com.prajan.cinehub.theatre.entity.Screen;
import com.prajan.cinehub.theatre.entity.Theatre;
import com.prajan.cinehub.theatre.mapper.ScreenMapper;
import com.prajan.cinehub.theatre.repository.ScreenRepository;
import com.prajan.cinehub.theatre.repository.TheatreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
@Transactional
public class ScreenService {

    private final ScreenRepository screenRepository;
    private final TheatreRepository theatreRepository;
    private final SeatService seatService;

    public ScreenResponse createScreen(CreateScreenRequest request) {

        Theatre theatre = theatreRepository.findById(request.getTheatreId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Theatre not found"));

        Screen screen = Screen.builder()
                .name(request.getName())
                .type(request.getType())
                .rows(request.getRows())
                .seatsPerRow(request.getSeatsPerRow())
                .totalSeats(request.getRows() * request.getSeatsPerRow())
                .active(true)
                .theatre(theatre)
                .build();

        Screen savedScreen = screenRepository.save(screen);

        seatService.generateSeats(
                savedScreen,
                request.getRows(),
                request.getSeatsPerRow()
        );

        return ScreenMapper.toResponse(savedScreen);
    }

}