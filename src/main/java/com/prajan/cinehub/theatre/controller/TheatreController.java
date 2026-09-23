package com.prajan.cinehub.theatre.controller;

import com.prajan.cinehub.theatre.dto.CreateTheatreRequest;
import com.prajan.cinehub.theatre.dto.UpdateTheatreRequest;
import com.prajan.cinehub.theatre.dto.TheatreResponse;
import com.prajan.cinehub.theatre.service.TheatreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/theatre_owner/theatres")
@RequiredArgsConstructor
public class TheatreController {

    private final TheatreService theatreService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TheatreResponse createTheatre(
            @Valid @RequestBody CreateTheatreRequest request) {

        return theatreService.createTheatre(request);
    }

    @GetMapping("/{id}")
    public TheatreResponse getTheatre(
            @PathVariable Long id) {

        return theatreService.getTheatre(id);
    }

    @GetMapping
    public List<TheatreResponse> getAllTheatres() {

        return theatreService.getAllTheatres();
    }

    @PutMapping("/{id}")
    public TheatreResponse updateTheatre(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTheatreRequest request) {

        return theatreService.updateTheatre(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTheatre(
            @PathVariable Long id) {

        theatreService.deleteTheatre(id);
    }

    @GetMapping("/city/{city}")
    public List<TheatreResponse> getByCity(
            @PathVariable String city) {

        return theatreService.getTheatresByCity(city);
    }

    @GetMapping("/active")
    public List<TheatreResponse> getActiveTheatres() {

        return theatreService.getActiveTheatres();
    }
}
