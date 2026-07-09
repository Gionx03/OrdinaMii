package com.example.ordinaMii.DTO.Response;

import com.example.ordinaMii.Entity.Enum.Roles;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UserResponseDTO(
        UUID id,
        String username,
        String email,
        Roles role,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}