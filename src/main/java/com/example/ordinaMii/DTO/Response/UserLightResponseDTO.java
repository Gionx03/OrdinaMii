package com.example.ordinaMii.DTO.Response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserLightResponseDTO(UUID id,
                                   String username,
                                   String email) {
}
