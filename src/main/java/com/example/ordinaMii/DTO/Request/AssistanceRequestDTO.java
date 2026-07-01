package com.example.ordinaMii.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;
@Builder
public record AssistanceRequestDTO(@NotBlank(message = "Il messaggio è obbligatorio")
                                   String message,

                                   @NotNull(message = "L'id del tavolo è obbligatorio")
                                   UUID tableId) {
}
