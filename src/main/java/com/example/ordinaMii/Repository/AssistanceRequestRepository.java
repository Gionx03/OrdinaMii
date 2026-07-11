package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.AssistanceRequest;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AssistanceRequestRepository extends JpaRepository<AssistanceRequest, UUID> {

    @Query("""
            SELECT ar
            FROM AssistanceRequest ar
            WHERE (:status IS NULL OR ar.status = :status)
            AND (:tableId IS NULL OR ar.table.id = :tableId)
            ORDER BY ar.createdAt DESC
            """)
    Page<AssistanceRequest> searchAssistanceRequests(
            @Param("status") AssistanceRequestStatus status,
            @Param("tableId") UUID tableId,
            Pageable pageable
    );
}