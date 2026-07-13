package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record DishResponseDTO(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        DishCategory category,
        boolean available,
        String imageUrl
) {
}