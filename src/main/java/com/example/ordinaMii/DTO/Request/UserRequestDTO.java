package com.example.ordinaMii.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserRequestDTO(@NotNull(message = "L'id dell'utente è obbligatorio")
                             UUID id,

                             @NotBlank(message = "Lo username è obbligatorio")
                             String username,

                             @NotBlank(message = "L'email è obbligatoria")
                             @Email(message = "L'email non è valida")
                             String email,

                             String phone) {

}
