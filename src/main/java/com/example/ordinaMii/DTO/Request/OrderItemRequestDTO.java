package com.example.ordinaMii.DTO.Request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;
@Builder
public record OrderItemRequestDTO(@NotNull(message = "L'id del piatto è obbligatorio")
                                  UUID dishId,
                                  @Min(value = 1, message = "La quantità deve essere almeno 1")
                                  int quantity
) {
}
