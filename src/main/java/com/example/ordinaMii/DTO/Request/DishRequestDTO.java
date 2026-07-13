package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DishRequestDTO(

        @NotBlank(message = "Il nome del piatto è obbligatorio")
        String name,

        String description,

        @NotNull(message = "Il prezzo è obbligatorio")
        @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
        BigDecimal price,

        @NotNull(message = "La categoria è obbligatoria")
        DishCategory category,

        boolean available,

        String imageUrl

) {
}