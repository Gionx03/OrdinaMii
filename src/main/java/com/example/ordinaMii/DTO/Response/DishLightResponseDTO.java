package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import lombok.Builder;

import java.util.UUID;

@Builder
public record DishLightResponseDTO(UUID id,
                                   String name,
                                   double price,
                                   boolean available,
                                   DishCategory category) {
}
