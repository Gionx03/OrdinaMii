package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record DishLightResponseDTO(
        UUID id,
        String name,
        BigDecimal price,
        boolean available,
        DishCategory category
) {
}