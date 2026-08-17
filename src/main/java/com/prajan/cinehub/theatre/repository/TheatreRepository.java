package com.prajan.cinehub.theatre.repository;

import com.prajan.cinehub.theatre.dto.TheatreResponse;
import com.prajan.cinehub.theatre.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByCityIgnoreCase(String city);

    List<Theatre> findByActiveTrue();

    List<TheatreResponse> getTheatresByCity(String city);

    boolean existsByNameAndCity(String name, String city);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);
}