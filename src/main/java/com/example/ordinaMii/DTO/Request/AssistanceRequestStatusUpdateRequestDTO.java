package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record AssistanceRequestStatusUpdateRequestDTO(@NotNull(message = "Lo stato della richiesta è obbligatorio")
                                               AssistanceRequestStatus status) {
}
