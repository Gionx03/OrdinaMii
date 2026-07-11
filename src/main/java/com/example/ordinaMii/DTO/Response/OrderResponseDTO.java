package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.OrderType;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
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
        OrderType orderType,
        UserLightResponseDTO user,
        RestaurantTableResponseDTO table,
        List<OrderItemResponseDTO> items
) {
}