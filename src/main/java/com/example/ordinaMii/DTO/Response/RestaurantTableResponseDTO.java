package com.example.ordinaMii.DTO.Response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record RestaurantTableResponseDTO(
        UUID id,
        int number,
        int seats,
        boolean active
) {
}