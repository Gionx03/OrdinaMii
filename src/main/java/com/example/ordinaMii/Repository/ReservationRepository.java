package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Query("""
            SELECT r
            FROM Reservation r
            WHERE (:status IS NULL OR r.status = :status)
            AND (:userId IS NULL OR r.user.id = :userId)
            AND (:tableId IS NULL OR r.table.id = :tableId)
            AND (:date IS NULL OR r.date = :date)
            """)
    Page<Reservation> searchReservations(
            @Param("status") ReservationStatus status,
            @Param("userId") UUID userId,
            @Param("tableId") UUID tableId,
            @Param("date") LocalDate date,
            Pageable pageable
    );
}
