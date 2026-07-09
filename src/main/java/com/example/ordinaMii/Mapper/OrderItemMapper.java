package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.OrderItemRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderItemResponseDTO;
import com.example.ordinaMii.Entity.Dish;
import com.example.ordinaMii.Entity.Order;
import com.example.ordinaMii.Entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderItemMapper {

    private final DishMapper dishMapper;

    public OrderItemMapper(DishMapper dishMapper) {
        this.dishMapper = dishMapper;
    }

    public OrderItem toEntity(OrderItemRequestDTO dto,
                              Dish dish,
                              Order order) {

        if (dto == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setQuantity(dto.quantity());
        orderItem.setDish(dish);
        orderItem.setOrder(order);

        if (dish != null) {
            orderItem.setUnitPrice(dish.getPrice());
        }

        return orderItem;
    }

    public OrderItemResponseDTO toResponseDTO(OrderItem orderItem) {

        if (orderItem == null) {
            return null;
        }

        return OrderItemResponseDTO.builder()
                .id(orderItem.getId())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .dish(dishMapper.toLightResponseDTO(orderItem.getDish()))
                .build();
    }

}