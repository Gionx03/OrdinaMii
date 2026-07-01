package com.example.ordinaMii.DTO.Response;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
@Builder
public record UserResponseDTO(UUID id,
                              String username,
                              String email,
                              String phone,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
