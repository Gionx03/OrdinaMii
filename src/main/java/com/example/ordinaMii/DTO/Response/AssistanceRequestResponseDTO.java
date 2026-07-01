package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AssistanceRequestResponseDTO(
        UUID id,
        String message,
        AssistanceRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime resolvedAt,
        RestaurantTableResponseDTO table
) {
}