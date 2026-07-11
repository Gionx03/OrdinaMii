package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.DTO.Response.ReservationResponseDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Enum.Roles;
import com.example.ordinaMii.Services.OrderService;
import com.example.ordinaMii.Services.ReservationService;
import com.example.ordinaMii.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final ReservationService reservationService;

    public UserController(UserService userService,
                          OrderService orderService,
                          ReservationService reservationService) {
        this.userService = userService;
        this.orderService = orderService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @RequestParam(required = false) Roles role,
            @PageableDefault(size = 10, sort = "username") Pageable pageable) {

        Page<UserResponseDTO> users = userService.getUsers(role, pageable);

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable UUID id) {

        UserResponseDTO user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getOrdersByUser(
            @PathVariable UUID id,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PageableDefault(size = 10, sort = "orderDate") Pageable pageable) {

        Page<OrderResponseDTO> orders = orderService.getOrdersByUser(id, status, startDate, pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}/reservations")
    public ResponseEntity<Page<ReservationResponseDTO>> getReservationsByUser(
            @PathVariable UUID id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {

        Page<ReservationResponseDTO> reservations =
                reservationService.getReservationsByUser(id, startDate, pageable);

        return ResponseEntity.ok(reservations);
    }
}