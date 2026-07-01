package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.DishRequestDTO;
import com.example.ordinaMii.DTO.Response.DishLightResponseDTO;
import com.example.ordinaMii.DTO.Response.DishResponseDTO;
import com.example.ordinaMii.Entity.Dish;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DishMapper {

    public Dish toEntity(DishRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        Dish dish = new Dish();

        dish.setName(dto.name());
        dish.setDescription(dto.description());
        dish.setPrice(dto.price());
        dish.setAvailable(dto.available());
        dish.setCategory(dto.category());

        return dish;
    }

    public void updateEntity(Dish dish, DishRequestDTO dto) {

        if (dish == null || dto == null) {
            return;
        }

        dish.setName(dto.name());
        dish.setDescription(dto.description());
        dish.setPrice(dto.price());
        dish.setAvailable(dto.available());
        dish.setCategory(dto.category());
    }

    public DishResponseDTO toResponseDTO(Dish dish) {

        if (dish == null) {
            return null;
        }

        return DishResponseDTO.builder()
                .id(dish.getId())
                .name(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .available(dish.isAvailable())
                .category(dish.getCategory())
                .build();
    }

    public DishLightResponseDTO toLightResponseDTO(Dish dish) {

        if (dish == null) {
            return null;
        }

        return DishLightResponseDTO.builder()
                .id(dish.getId())
                .name(dish.getName())
                .price(dish.getPrice())
                .available(dish.isAvailable())
                .category(dish.getCategory())
                .build();
    }

    public List<DishResponseDTO> toResponseDTOList(List<Dish> dishes) {

        List<DishResponseDTO> dtoList = new ArrayList<>();

        if (dishes == null) {
            return dtoList;
        }

        for (Dish dish : dishes) {
            dtoList.add(toResponseDTO(dish));
        }

        return dtoList;
    }
}