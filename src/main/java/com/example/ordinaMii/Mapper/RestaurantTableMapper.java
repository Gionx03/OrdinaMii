package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.RestaurantTableRequestDTO;
import com.example.ordinaMii.DTO.Response.RestaurantTableResponseDTO;
import com.example.ordinaMii.Entity.RestaurantTable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RestaurantTableMapper {

    public RestaurantTable toEntity(RestaurantTableRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        RestaurantTable table = new RestaurantTable();

        table.setNumber(dto.number());
        table.setSeats(dto.seats());

        return table;
    }

    public void updateEntity(RestaurantTable table, RestaurantTableRequestDTO dto) {

        if (table == null || dto == null) {
            return;
        }

        table.setNumber(dto.number());
        table.setSeats(dto.seats());
    }

    public RestaurantTableResponseDTO toResponseDTO(RestaurantTable table) {

        if (table == null) {
            return null;
        }

        return RestaurantTableResponseDTO.builder()
                .id(table.getId())
                .number(table.getNumber())
                .seats(table.getSeats())
                .build();
    }

    public List<RestaurantTableResponseDTO> toResponseDTOList(List<RestaurantTable> tables) {

        List<RestaurantTableResponseDTO> dtoList = new ArrayList<>();

        if (tables == null) {
            return dtoList;
        }

        for (RestaurantTable table : tables) {
            dtoList.add(toResponseDTO(table));
        }

        return dtoList;
    }
}