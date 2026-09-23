package com.prajan.cinehub.show.controller;

import com.prajan.cinehub.show.dto.CreateShowRequest;
import com.prajan.cinehub.show.dto.ShowResponse;
import com.prajan.cinehub.show.dto.ShowSeatResponse;
import com.prajan.cinehub.show.dto.UpdateShowRequest;
import com.prajan.cinehub.show.service.ShowSeatService;
import com.prajan.cinehub.show.service.ShowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class ShowController {

    private final ShowService showService;
    private final ShowSeatService showSeatService;

    @PostMapping("/shows")
    public ResponseEntity<ShowResponse> createShow(
            @RequestBody CreateShowRequest request) {

        ShowResponse response =
                showService.createShow(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/shows/{showId}")
    public ResponseEntity<ShowResponse> getShow(
            @PathVariable Long showId) {

        return ResponseEntity.ok(
                showService.getShowById(showId)
        );
    }

    @PutMapping("/shows/{showId}")
    public ResponseEntity<ShowResponse> updateShow(
            @PathVariable Long showId,
            @RequestBody UpdateShowRequest request) {

        return ResponseEntity.ok(
                showService.updateShow(showId, request)
        );
    }

    @PatchMapping("/shows/{showId}/cancel")
    public ResponseEntity<Void> cancelShow(
            @PathVariable Long showId) {

        showService.cancelShow(showId);

        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/public/movies/{movieId}/shows")
//    public ResponseEntity<?> getShowsByMovie(
//            @PathVariable Long movieId) {
//
//        return ResponseEntity.ok(
//                showService.getAvailableShowsByMovie(movieId)
//        );
//    }

    @GetMapping("/shows/{showId}/seats")
    public List<ShowSeatResponse> getShowSeats(
            @PathVariable Long showId
    ) {
        return showSeatService.getSeatsByShow(showId);
    }

    @GetMapping("/public/shows/movies/{movieId}")
    public ResponseEntity<?> getShowsByMovie(
            @PathVariable Long movieId) {
        log.info("getting shows for movie id: {}", movieId);

        return ResponseEntity.ok(
                showService.getAvailableShowsByMovie(movieId)
        );
    }
}