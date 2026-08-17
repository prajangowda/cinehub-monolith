package com.prajan.cinehub.theatre.controller;

import com.prajan.cinehub.theatre.dto.CreateScreenRequest;
import com.prajan.cinehub.theatre.dto.ScreenResponse;
import com.prajan.cinehub.theatre.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/screens")
@RequiredArgsConstructor
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping
    public ResponseEntity<ScreenResponse> createScreen(
            @RequestBody CreateScreenRequest request) {

        ScreenResponse response = screenService.createScreen(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
