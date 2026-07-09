package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.OrderRequestDTO;
import com.example.ordinaMii.DTO.Request.OrderStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderResponseDTO> getOrders(OrderStatus status, UUID customerId, LocalDate data) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public OrderResponseDTO getOrderById(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public Page<OrderResponseDTO> getOrdersByTable(UUID tableId, LocalDate data,Pageable pageable) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public OrderResponseDTO updateOrder(UUID id, OrderRequestDTO orderRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public OrderResponseDTO updateOrderStatus(UUID id, OrderStatusUpdateRequestDTO orderStatusUpdateRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public void deleteOrder(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public Page<OrderResponseDTO> getMyOrders(UUID userId, OrderStatus status, LocalDate data, Pageable pageable) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }
}
