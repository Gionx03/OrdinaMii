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
import java.time.LocalTime;
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
            ORDER BY r.date ASC, r.time ASC
            """)
    Page<Reservation> searchReservations(
            @Param("status") ReservationStatus status,
            @Param("userId") UUID userId,
            @Param("tableId") UUID tableId,
            @Param("date") LocalDate date,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM Reservation r
            WHERE (:status IS NULL OR r.status = :status)
            AND (:userId IS NULL OR r.user.id = :userId)
            AND (:tableId IS NULL OR r.table.id = :tableId)
            AND (:startDate IS NULL OR r.date >= :startDate)
            ORDER BY r.date ASC, r.time ASC
            """)
    Page<Reservation> searchReservationsFromDate(
            @Param("status") ReservationStatus status,
            @Param("userId") UUID userId,
            @Param("tableId") UUID tableId,
            @Param("startDate") LocalDate startDate,
            Pageable pageable
    );

    boolean existsByTable_IdAndDateAndTimeAndStatusNot(
            UUID tableId,
            LocalDate date,
            LocalTime time,
            ReservationStatus status
    );

    boolean existsByTable_IdAndDateAndTimeAndIdNotAndStatusNot(
            UUID tableId,
            LocalDate date,
            LocalTime time,
            UUID id,
            ReservationStatus status
    );


    @Query("""
        SELECT COUNT(r) > 0
        FROM Reservation r
        WHERE r.table.id = :tableId
        AND r.status <> :cancelledStatus
        AND (
            r.date > :currentDate
            OR (r.date = :currentDate AND r.time >= :currentTime)
        )
        """)
    boolean existsFutureActiveReservationByTable(
            @Param("tableId") UUID tableId,
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime,
            @Param("cancelledStatus") ReservationStatus cancelledStatus
    );
}