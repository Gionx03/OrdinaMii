package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.ReservationRequestDTO;
import com.example.ordinaMii.DTO.Request.ReservationStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.ReservationResponseDTO;
import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Services.ReservationService;
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
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponseDTO>> getReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(name = "user_id", required = false) UUID userId,
            @RequestParam(name = "table_id", required = false) UUID tableId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @PageableDefault(size = 10, sort = "date") Pageable pageable) {

        Page<ReservationResponseDTO> reservations =
                reservationService.getReservations(status, userId, tableId, data,pageable);

        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable UUID id) {
        ReservationResponseDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByTable(
            @PathVariable UUID tableId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {

        List<ReservationResponseDTO> reservations =
                reservationService.getReservationsByTable(tableId, data);

        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {

        ReservationResponseDTO reservation =
                reservationService.createReservation(reservationRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable UUID id,
            @Valid @RequestBody ReservationRequestDTO reservationRequestDTO) {

        ReservationResponseDTO reservation =
                reservationService.updateReservation(id, reservationRequestDTO);

        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationResponseDTO> updateReservationStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ReservationStatusUpdateRequestDTO reservationStatusUpdateRequestDTO) {

        ReservationResponseDTO reservation =
                reservationService.updateReservationStatus(id, reservationStatusUpdateRequestDTO);

        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable UUID id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}