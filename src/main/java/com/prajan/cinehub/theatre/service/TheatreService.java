package com.prajan.cinehub.theatre.service;

import com.prajan.cinehub.auth.enums.Role;
import com.prajan.cinehub.auth.model.UserIn;
import com.prajan.cinehub.error.*;
import com.prajan.cinehub.theatre.dto.CreateTheatreRequest;
import com.prajan.cinehub.theatre.dto.UpdateTheatreRequest;
import com.prajan.cinehub.theatre.dto.TheatreResponse;
import com.prajan.cinehub.theatre.entity.Theatre;
import com.prajan.cinehub.theatre.mapper.TheatreMapper;
import com.prajan.cinehub.theatre.repository.TheatreRepository;

import com.prajan.cinehub.auth.repository.UserInRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class  TheatreService {

    private final TheatreRepository theatreRepository;
    private final UserInRepository userRepository;


    public TheatreResponse createTheatre(CreateTheatreRequest request) {

        if (theatreRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already exists");
        }

        UserIn owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Owner not found"));

        if (owner.getRole() != Role.THEATRE_OWNER) {
            throw new IllegalArgumentException("User is not a theatre owner");
        }

        Theatre theatre = Theatre.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .phone(request.getPhone())
                .email(request.getEmail())
                .active(true)
                .owner(owner)
                .build();

        theatreRepository.save(theatre);

        return TheatreMapper.toResponse(theatre);
    }


    public TheatreResponse getTheatre(Long theatreId) {

        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Theatre not found"));

        return TheatreMapper.toResponse(theatre);
    }


    public List<TheatreResponse> getAllTheatres() {

        return theatreRepository.findAll()
                .stream()
                .map(TheatreMapper::toResponse)
                .toList();
    }


    public void deleteTheatre(Long theatreId) {

        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Theatre not found"));

        theatre.setActive(false);

        theatreRepository.save(theatre);
    }


    public TheatreResponse updateTheatre(Long theatreId,
                                         UpdateTheatreRequest request) {

        Theatre theatre = theatreRepository.findById(theatreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Theatre not found"));

        if (request.getName() != null)
            theatre.setName(request.getName());

        if (request.getAddress() != null)
            theatre.setAddress(request.getAddress());

        if (request.getCity() != null)
            theatre.setCity(request.getCity());

        if (request.getState() != null)
            theatre.setState(request.getState());

        if (request.getPincode() != null)
            theatre.setPincode(request.getPincode());

        if (request.getLatitude() != null)
            theatre.setLatitude(request.getLatitude());

        if (request.getLongitude() != null)
            theatre.setLongitude(request.getLongitude());

        if (request.getPhone() != null)
            theatre.setPhone(request.getPhone());

        if (request.getEmail() != null)
            theatre.setEmail(request.getEmail());

        if (request.getActive() != null)
            theatre.setActive(request.getActive());

        theatreRepository.save(theatre);

        return TheatreMapper.toResponse(theatre);
    }


    public List<TheatreResponse> getTheatresByCity(String city) {

        return theatreRepository.findByCityIgnoreCase(city)
                .stream()
                .map(TheatreMapper::toResponse)
                .toList();
    }


    public List<TheatreResponse> getActiveTheatres() {

        return theatreRepository.findByActiveTrue()
                .stream()
                .map(TheatreMapper::toResponse)
                .toList();
    }
}