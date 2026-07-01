package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import lombok.Builder;

import java.util.UUID;
@Builder
public record DishResponseDTO(UUID id,
                              String name,
                              String description,
                              double price,
                              boolean available,
                              DishCategory category) {
}
