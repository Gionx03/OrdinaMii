package com.example.ordinaMii.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateMeRequestDTO(
        @NotBlank(message = "Il numero di telefono è obbligatorio")
        String phone
) {
}