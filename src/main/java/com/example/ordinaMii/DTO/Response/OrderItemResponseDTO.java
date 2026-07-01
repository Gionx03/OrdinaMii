package com.example.ordinaMii.DTO.Response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record OrderItemResponseDTO(
        UUID id,
        int quantity,
        double unitPrice,
        DishLightResponseDTO dish
) {
}