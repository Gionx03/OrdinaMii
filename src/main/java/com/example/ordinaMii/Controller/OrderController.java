package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.OrderRequestDTO;
import com.example.ordinaMii.DTO.Request.OrderStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Request.PaymentStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Services.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponseDTO>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(name = "customer_id", required = false) UUID customerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @PageableDefault(size=10, sort="orderDate") Pageable pageable) {

        Page<OrderResponseDTO> orders = orderService.getOrders(status, customerId, data,pageable);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrderById(@PathVariable UUID id) {
        OrderResponseDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }


    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody OrderRequestDTO orderRequestDTO) {
        OrderResponseDTO order = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> updateOrder(
            @PathVariable UUID id,
            @Valid @RequestBody OrderRequestDTO orderRequestDTO) {

        OrderResponseDTO order = orderService.updateOrder(id, orderRequestDTO);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateOrderStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrderStatusUpdateRequestDTO orderStatusUpdateRequestDTO) {

        OrderResponseDTO order = orderService.updateOrderStatus(id, orderStatusUpdateRequestDTO);
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> deleteOrder(@PathVariable UUID id) {

        OrderResponseDTO order = orderService.deleteOrder(id);

        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/payment-status")
    public ResponseEntity<OrderResponseDTO> updatePaymentStatus(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentStatusUpdateRequestDTO paymentStatusUpdateRequestDTO) {

        OrderResponseDTO order = orderService.updatePaymentStatus(id, paymentStatusUpdateRequestDTO);
        return ResponseEntity.ok(order);
    }
}