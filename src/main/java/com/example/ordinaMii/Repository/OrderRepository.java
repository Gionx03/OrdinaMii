package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query(value = """
            SELECT *
            FROM orders o
            WHERE (:status IS NULL OR o.status = :status)
            AND (:customerId IS NULL OR o.user_id = :customerId)
            AND (:data IS NULL OR CAST(o.order_date AS DATE) = :data)
            ORDER BY o.order_date DESC
            """, nativeQuery = true)
    List<Order> searchOrders(@Param("status") String status,
                             @Param("customerId") UUID customerId,
                             @Param("data") LocalDate data);

    @Query(value = """
            SELECT o.*
            FROM orders o
            INNER JOIN reservation r
            ON o.reservation_id = r.id
            WHERE r.table_id = :tableId
            AND (:data IS NULL OR CAST(o.order_date AS DATE) = :data)
            ORDER BY o.order_date DESC
            """, nativeQuery = true)
    List<Order> findOrdersByTable(@Param("tableId") UUID tableId,
                                  @Param("data") LocalDate data);

    @Query(value = """
            SELECT *
            FROM orders o
            WHERE o.user_id = :userId
            AND (:status IS NULL OR o.status = :status)
            AND (:data IS NULL OR CAST(o.order_date AS DATE) = :data)
            ORDER BY o.order_date DESC
            """, nativeQuery = true)
    List<Order> findMyOrders(@Param("userId") UUID userId,
                             @Param("status") String status,
                             @Param("data") LocalDate data);
}
