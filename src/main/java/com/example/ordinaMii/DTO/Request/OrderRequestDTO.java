package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.Roles;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record OrderRequestDTO(@NotNull(message = "L'id dell'utente è obbligatorio")
                              UUID userId,

                              @NotNull(message = "Il ruolo è obbligatorio")
                              Roles role,

                              @NotEmpty(message = "L'ordine deve contenere almeno un piatto")
                              List<@Valid OrderItemRequestDTO> items) {
}
