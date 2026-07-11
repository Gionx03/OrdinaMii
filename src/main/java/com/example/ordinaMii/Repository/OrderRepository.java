package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Enum.OrderStatus;
import com.example.ordinaMii.Entity.CustomerOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<CustomerOrder, UUID> {

    @Query("""
            SELECT o
            FROM CustomerOrder o
            WHERE (:status IS NULL OR o.status = :status)
            AND (:userId IS NULL OR o.user.id = :userId)
            AND (:startDateTime IS NULL OR o.orderDate >= :startDateTime)
            AND (:endDateTime IS NULL OR o.orderDate < :endDateTime)
            """)
    Page<CustomerOrder> searchOrders(
            @Param("status") OrderStatus status,
            @Param("userId") UUID userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
    @Query("""
        SELECT o
        FROM CustomerOrder o
        WHERE (:status IS NULL OR o.status = :status)
        AND o.user.id = :userId
        AND (:startDateTime IS NULL OR o.orderDate >= :startDateTime)
        """)
    Page<CustomerOrder> searchOrdersByUserFromDate(
            @Param("status") OrderStatus status,
            @Param("userId") UUID userId,
            @Param("startDateTime") LocalDateTime startDateTime,
            Pageable pageable
    );
}
