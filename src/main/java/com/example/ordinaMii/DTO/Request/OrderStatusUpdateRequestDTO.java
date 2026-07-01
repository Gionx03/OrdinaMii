package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record OrderStatusUpdateRequestDTO(@NotNull(message = "Lo stato dell'ordine è obbligatorio")
                                          OrderStatus status) {
}
