package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.OrderItemRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderItemResponseDTO;
import com.example.ordinaMii.Entity.Dish;
import com.example.ordinaMii.Entity.CustomerOrder;
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
                              CustomerOrder customerOrder) {

        if (dto == null) {
            return null;
        }

        OrderItem orderItem = new OrderItem();

        orderItem.setQuantity(dto.quantity());
        orderItem.setDish(dish);
        orderItem.setCustomerOrder(customerOrder);

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

    public List<OrderItemResponseDTO> toResponseDTOList(List<OrderItem> orderItems) {

        List<OrderItemResponseDTO> dtoList = new ArrayList<>();

        if (orderItems == null) {
            return dtoList;
        }

        for (OrderItem orderItem : orderItems) {
            dtoList.add(toResponseDTO(orderItem));
        }

        return dtoList;
    }
}