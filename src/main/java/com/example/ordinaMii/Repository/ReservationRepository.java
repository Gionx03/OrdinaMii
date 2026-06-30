package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    @Query(value = """
            SELECT *
            FROM reservation r
            WHERE (:userId IS NULL OR r.user_id = :userId)
            AND (:tableId IS NULL OR r.table_id = :tableId)
            AND (:status IS NULL OR r.status = :status)
            AND (:data IS NULL OR CAST(r.date AS DATE) = :data)
            ORDER BY r.date DESC
            """, nativeQuery = true)
    List<Reservation> searchReservations(@Param("userId") UUID userId,
                                         @Param("tableId") UUID tableId,
                                         @Param("status") String status,
                                         @Param("data") LocalDate data);
}
