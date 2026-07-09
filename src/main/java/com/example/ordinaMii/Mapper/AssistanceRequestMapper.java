package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.AssistanceRequestDTO;
import com.example.ordinaMii.DTO.Response.AssistanceRequestResponseDTO;
import com.example.ordinaMii.Entity.AssistanceRequest;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import com.example.ordinaMii.Entity.RestaurantTable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class AssistanceRequestMapper {

    private final RestaurantTableMapper restaurantTableMapper;

    public AssistanceRequestMapper(RestaurantTableMapper restaurantTableMapper) {
        this.restaurantTableMapper = restaurantTableMapper;
    }

    public AssistanceRequest toEntity(AssistanceRequestDTO dto,
                                      RestaurantTable table,
                                      AssistanceRequestStatus status) {

        if (dto == null) {
            return null;
        }

        AssistanceRequest assistanceRequest = new AssistanceRequest();

        assistanceRequest.setMessage(dto.message());
        assistanceRequest.setTable(table);
        assistanceRequest.setStatus(status);
        assistanceRequest.setCreatedAt(LocalDateTime.now());

        return assistanceRequest;
    }

    public AssistanceRequestResponseDTO toResponseDTO(AssistanceRequest assistanceRequest) {

        if (assistanceRequest == null) {
            return null;
        }

        return AssistanceRequestResponseDTO.builder()
                .id(assistanceRequest.getId())
                .message(assistanceRequest.getMessage())
                .status(assistanceRequest.getStatus())
                .createdAt(assistanceRequest.getCreatedAt())
                .resolvedAt(assistanceRequest.getResolvedAt())
                .table(restaurantTableMapper.toResponseDTO(assistanceRequest.getTable()))
                .build();
    }

}