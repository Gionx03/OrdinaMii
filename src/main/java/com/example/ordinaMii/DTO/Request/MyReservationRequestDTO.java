package com.example.ordinaMii.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Builder
public record MyReservationRequestDTO(

        @NotNull(message = "La data è obbligatoria")
        @FutureOrPresent(message = "La data non può essere nel passato")
        LocalDate date,

        @NotNull(message = "L'orario è obbligatorio")
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,

        @Min(value = 1, message = "Il numero di persone deve essere almeno 1")
        int numberOfPeople,

        @NotNull(message = "L'id del tavolo è obbligatorio")
        UUID tableId

) {
}