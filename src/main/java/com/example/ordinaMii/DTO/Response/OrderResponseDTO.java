package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import com.example.ordinaMii.Entity.Enum.Roles;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record OrderResponseDTO(
        UUID id,
        LocalDateTime orderDate,
        BigDecimal total,
        OrderStatus status,
        PaymentStatus paymentStatus,
        Roles role,
        UserLightResponseDTO user,
        List<OrderItemResponseDTO> items
) {
}