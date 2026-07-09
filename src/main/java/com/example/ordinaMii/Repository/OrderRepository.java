package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("""
            SELECT o
            FROM Order o
            WHERE (:status IS NULL OR o.status = :status)
            AND (:userId IS NULL OR o.user.id = :userId)
            AND (:startDateTime IS NULL OR o.orderDate >= :startDateTime)
            AND (:endDateTime IS NULL OR o.orderDate < :endDateTime)
            """)
    Page<Order> searchOrders(
            @Param("status") OrderStatus status,
            @Param("userId") UUID userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );


    @Query(value = """
            SELECT *
            FROM Orders o
            WHERE o.user_id = :userId
            AND (:status IS NULL OR o.status = :status)
            AND (:data IS NULL OR CAST(o.order_date AS DATE) = :data)
            ORDER BY o.order_date DESC
            """, nativeQuery = true)
    Page<Order> findMyOrders(@Param("userId") UUID userId,
                             @Param("status") String status,
                             @Param("data") LocalDate data,
                             Pageable pageable);
}
