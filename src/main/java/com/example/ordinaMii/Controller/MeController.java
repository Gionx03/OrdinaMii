package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.MyOrderRequestDTO;
import com.example.ordinaMii.DTO.Request.MyReservationRequestDTO;
import com.example.ordinaMii.DTO.Request.UpdateMeRequestDTO;
import com.example.ordinaMii.DTO.Response.OrderResponseDTO;
import com.example.ordinaMii.DTO.Response.ReservationResponseDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Services.OrderService;
import com.example.ordinaMii.Services.ReservationService;
import com.example.ordinaMii.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/me")
public class MeController {

    private final UserService userService;
    private final OrderService orderService;
    private final ReservationService reservationService;

    public MeController(UserService userService,
                        OrderService orderService,
                        ReservationService reservationService) {
        this.userService = userService;
        this.orderService = orderService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getMe(@AuthenticationPrincipal Jwt jwt) {

        UserResponseDTO user = userService.getOrCreateMe(jwt);

        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<UserResponseDTO> updateMe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UpdateMeRequestDTO updateMeRequestDTO) {

        UserResponseDTO user = userService.updateMe(jwt, updateMeRequestDTO);

        return ResponseEntity.ok(user);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponseDTO>> getMyOrders(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PageableDefault(size = 10, sort = "orderDate") Pageable pageable) {

        UserResponseDTO user = userService.getOrCreateMe(jwt);

        Page<OrderResponseDTO> orders = orderService.getMyOrders(user.id(), status, startDate, pageable);

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/reservations")
    public ResponseEntity<Page<ReservationResponseDTO>> getMyReservations(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {

        UserResponseDTO user = userService.getOrCreateMe(jwt);

        Page<ReservationResponseDTO> reservations =
                reservationService.getReservationsByUser(user.id(), startDate, pageable);

        return ResponseEntity.ok(reservations);
    }



    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseDTO> createMyReservation(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MyReservationRequestDTO myReservationRequestDTO) {

        UserResponseDTO user = userService.getOrCreateMe(jwt);

        ReservationResponseDTO reservation = reservationService.createMyReservation(
                user.id(),
                myReservationRequestDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponseDTO> createMyOrder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MyOrderRequestDTO myOrderRequestDTO) {

        UserResponseDTO user = userService.getOrCreateMe(jwt);

        OrderResponseDTO order = orderService.createMyOrder(
                user.id(),
                myOrderRequestDTO
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}