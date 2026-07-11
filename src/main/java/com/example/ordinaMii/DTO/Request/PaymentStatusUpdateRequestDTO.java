package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaymentStatusUpdateRequestDTO(@NotNull(message = "Lo stato del pagamento è obbligatorio")
                                          PaymentStatus paymentStatus) {
}
