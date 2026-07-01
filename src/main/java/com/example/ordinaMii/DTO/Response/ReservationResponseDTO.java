package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record ReservationResponseDTO(
        UUID id,
        LocalDate date,

        @JsonFormat(pattern = "HH:mm")
        LocalTime time,

        int numberOfPeople,
        ReservationStatus status,
        UserLightResponseDTO user,
        RestaurantTableResponseDTO table
) {
}