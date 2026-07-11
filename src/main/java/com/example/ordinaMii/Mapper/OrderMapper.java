package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.OrderRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.Entity.CustomerOrder;
import com.example.ordinaMii.Entity.OrderItem;
import com.example.ordinaMii.Entity.RestaurantTable;
import com.example.ordinaMii.Entity.User;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.OrderType;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    private final UserMapper userMapper;
    private final OrderItemMapper orderItemMapper;
    private final RestaurantTableMapper restaurantTableMapper;

    public OrderMapper(UserMapper userMapper,
                       OrderItemMapper orderItemMapper,
                       RestaurantTableMapper restaurantTableMapper) {
        this.userMapper = userMapper;
        this.orderItemMapper = orderItemMapper;
        this.restaurantTableMapper = restaurantTableMapper;
    }

    public CustomerOrder toEntity(OrderRequestDTO dto,
                                  User user,
                                  RestaurantTable table,
                                  BigDecimal total,
                                  OrderStatus status,
                                  PaymentStatus paymentStatus) {

        if (dto == null) {
            return null;
        }

        return toEntity(
                user,
                table,
                total,
                status,
                paymentStatus,
                dto.orderType()
        );
    }

    public CustomerOrder toEntity(User user,
                                  RestaurantTable table,
                                  BigDecimal total,
                                  OrderStatus status,
                                  PaymentStatus paymentStatus,
                                  OrderType orderType) {

        CustomerOrder customerOrder = new CustomerOrder();

        customerOrder.setOrderDate(LocalDateTime.now());
        customerOrder.setTotal(total);
        customerOrder.setStatus(status);
        customerOrder.setPaymentStatus(paymentStatus);
        customerOrder.setOrderType(orderType);
        customerOrder.setTable(table);
        customerOrder.setUser(user);

        return customerOrder;
    }

    public OrderResponseDTO toResponseDTO(CustomerOrder customerOrder,
                                          List<OrderItem> orderItems) {

        if (customerOrder == null) {
            return null;
        }

        return OrderResponseDTO.builder()
                .id(customerOrder.getId())
                .orderDate(customerOrder.getOrderDate())
                .total(customerOrder.getTotal())
                .status(customerOrder.getStatus())
                .paymentStatus(customerOrder.getPaymentStatus())
                .orderType(customerOrder.getOrderType())
                .user(userMapper.toLightResponseDTO(customerOrder.getUser()))
                .table(restaurantTableMapper.toResponseDTO(customerOrder.getTable()))
                .items(orderItemMapper.toResponseDTOList(orderItems))
                .build();
    }

    public OrderResponseDTO toResponseDTO(CustomerOrder customerOrder) {

        if (customerOrder == null) {
            return null;
        }

        return OrderResponseDTO.builder()
                .id(customerOrder.getId())
                .orderDate(customerOrder.getOrderDate())
                .total(customerOrder.getTotal())
                .status(customerOrder.getStatus())
                .paymentStatus(customerOrder.getPaymentStatus())
                .orderType(customerOrder.getOrderType())
                .user(userMapper.toLightResponseDTO(customerOrder.getUser()))
                .table(restaurantTableMapper.toResponseDTO(customerOrder.getTable()))
                .items(new ArrayList<>())
                .build();
    }
}