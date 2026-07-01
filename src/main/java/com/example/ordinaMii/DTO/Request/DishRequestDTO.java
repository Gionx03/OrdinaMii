package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DishRequestDTO(@NotBlank(message = "Il nome del piatto è obbligatorio")
                             String name,

                             String description,

                             @Min(value = 0, message = "Il prezzo non può essere negativo")
                             double price,

                             @NotNull(message = "La disponibilità è obbligatoria")
                             Boolean available,

                             @NotNull(message = "La categoria è obbligatoria")
                             DishCategory category) {
}
