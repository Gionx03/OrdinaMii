package com.example.ordinaMii.DTO.Request;

import com.example.ordinaMii.Entity.Enum.OrderType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record MyOrderRequestDTO(

        @NotNull(message = "Il tipo di ordine è obbligatorio")
        OrderType orderType,

        UUID tableId,

        @NotEmpty(message = "L'ordine deve contenere almeno un piatto")
        List<@Valid OrderItemRequestDTO> items

) {
}