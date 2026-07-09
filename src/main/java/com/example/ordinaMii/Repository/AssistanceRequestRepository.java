package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.AssistanceRequest;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssistanceRequestRepository extends JpaRepository<AssistanceRequest, UUID> {

    Page<AssistanceRequest> findByStatus(AssistanceRequestStatus status, Pageable pageable);

    Page<AssistanceRequest> findByTableId(UUID tableId, Pageable pageable);
}