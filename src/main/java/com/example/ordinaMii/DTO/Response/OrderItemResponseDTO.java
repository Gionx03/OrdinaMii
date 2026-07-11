package com.example.ordinaMii.DTO.Response;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record OrderItemResponseDTO(
        UUID id,
        int quantity,
        BigDecimal unitPrice,
        DishLightResponseDTO dish
) {
}