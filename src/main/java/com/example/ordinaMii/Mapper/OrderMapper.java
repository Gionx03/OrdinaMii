package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.OrderRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.PaymentStatus;
import com.example.ordinaMii.Entity.Order;
import com.example.ordinaMii.Entity.OrderItem;
import com.example.ordinaMii.Entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class OrderMapper {

    private final UserMapper userMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderMapper(UserMapper userMapper,
                       OrderItemMapper orderItemMapper) {
        this.userMapper = userMapper;
        this.orderItemMapper = orderItemMapper;
    }

    public Order toEntity(OrderRequestDTO dto,
                          User user,
                          BigDecimal total,
                          OrderStatus status,
                          PaymentStatus paymentStatus) {

        if (dto == null) {
            return null;
        }

        Order order = new Order();

        order.setOrderDate(LocalDateTime.now());
        order.setTotal(total);
        order.setStatus(status);
        order.setPaymentStatus(paymentStatus);
        order.setUser(user);

        return order;
    }

    public OrderResponseDTO toResponseDTO(Order order,
                                          List<OrderItem> orderItems) {

        if (order == null) {
            return null;
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .total(order.getTotal())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .user(userMapper.toLightResponseDTO(order.getUser()))
                .items(orderItemMapper.toResponseDTOList(orderItems))
                .build();
    }

    public OrderResponseDTO toResponseDTO(Order order) {

        if (order == null) {
            return null;
        }

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .total(order.getTotal())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .user(userMapper.toLightResponseDTO(order.getUser()))
                .items(new ArrayList<>())
                .build();
    }

}