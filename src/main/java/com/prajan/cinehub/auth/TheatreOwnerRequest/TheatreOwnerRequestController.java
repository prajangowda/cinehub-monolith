package com.prajan.cinehub.auth.TheatreOwnerRequest;

import com.prajan.cinehub.auth.model.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class TheatreOwnerRequestController {

    private final TheatreOwnerRequestService service;

    @PostMapping("/owner/request")
    public TheatreOwnerResponseDto submit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody TheatreOwnerRequest request
    ) {


        return service.submitRequest(userDetails.getId(), request);
    }

    @GetMapping("/admin/owner-requests")
    public List<TheatreOwnerResponseDto> pendingRequests() {
        return service.getPendingRequests();
    }

    @PutMapping("/admin/owner-requests/{id}/approve")
    public void approve(@PathVariable Long id) {
        service.approve(id);
    }

    @PutMapping("/admin/owner-requests/{id}/reject")
    public void reject(@PathVariable Long id) {
        service.reject(id);
    }
}