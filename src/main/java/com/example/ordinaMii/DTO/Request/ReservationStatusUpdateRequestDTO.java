package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ReservationStatusUpdateRequestDTO(@NotNull(message = "Lo stato della prenotazione è obbligatorio")
                                                ReservationStatus status) {
}
