package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Services.OrderService;
import com.example.ordinaMii.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/me")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UserResponseDTO user = userService.getMe(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @PageableDefault(size = 10, sort = "orderDate") Pageable pageable) {

        UUID userId = UUID.fromString(jwt.getSubject());
        Page<OrderResponseDTO> orders = orderService.getMyOrders(userId, status, data,pageable);
        return ResponseEntity.ok(orders);
    }
}