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
        SELECT c
        FROM CustomerOrder c
        WHERE (:status IS NULL OR c.status = :status)
        AND (:customerId IS NULL OR c.user.id = :customerId)
        AND c.orderDate >= :startDate
        AND c.orderDate < :endDate
        ORDER BY c.orderDate DESC
        """)
    Page<CustomerOrder> searchOrders(
            @Param("status") OrderStatus status,
            @Param("customerId") UUID customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


    @Query("""
        SELECT c
        FROM CustomerOrder c
        WHERE c.user.id = :userId
        AND (:status IS NULL OR c.status = :status)
        AND c.orderDate >= :startDate
        ORDER BY c.orderDate DESC
        """)
    Page<CustomerOrder> searchOrdersByUserFromDate(
            @Param("userId") UUID userId,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            Pageable pageable
    );
}
