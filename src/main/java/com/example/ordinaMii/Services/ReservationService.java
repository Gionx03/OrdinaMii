package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.ReservationRequestDTO;
import com.example.ordinaMii.DTO.Request.ReservationStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.ReservationResponseDTO;
import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Mapper.ReservationMapper;
import com.example.ordinaMii.Repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public ReservationService(ReservationRepository reservationRepository, ReservationMapper reservationMapper) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
    }

    public Page<ReservationResponseDTO> getReservations(
            ReservationStatus status,
            UUID userId,
            UUID tableId,
            LocalDate data,
            Pageable pageable) {

        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public ReservationResponseDTO getReservationById(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public List<ReservationResponseDTO> getReservationsByTable(UUID tableId, LocalDate data) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public ReservationResponseDTO updateReservation(UUID id, ReservationRequestDTO reservationRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public ReservationResponseDTO updateReservationStatus(
            UUID id,
            ReservationStatusUpdateRequestDTO reservationStatusUpdateRequestDTO) {

        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public void deleteReservation(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }
}
