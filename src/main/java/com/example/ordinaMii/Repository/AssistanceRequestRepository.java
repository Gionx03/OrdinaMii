package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.AssistanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AssistanceRequestRepository extends JpaRepository<AssistanceRequest, UUID> {

    List<AssistanceRequest> findByStatus(String status);

    List<AssistanceRequest> findByTableId(UUID tableId);
}