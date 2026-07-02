package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.AssistanceRequestDTO;
import com.example.ordinaMii.DTO.Request.AssistanceRequestStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.AssistanceRequestResponseDTO;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import com.example.ordinaMii.Repository.AssistanceRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AssistanceRequestService {

    private final AssistanceRequestRepository assistanceRequestRepository;

    public AssistanceRequestService(AssistanceRequestRepository assistanceRequestRepository) {
        this.assistanceRequestRepository = assistanceRequestRepository;
    }

    public List<AssistanceRequestResponseDTO> getAssistanceRequests(AssistanceRequestStatus status, UUID tableId) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public AssistanceRequestResponseDTO getAssistanceRequestById(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public AssistanceRequestResponseDTO createAssistanceRequest(AssistanceRequestDTO assistanceRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public AssistanceRequestResponseDTO updateAssistanceRequestStatus(
            UUID id,
            AssistanceRequestStatusUpdateRequestDTO assistanceRequestStatusUpdateRequestDTO) {

        throw new UnsupportedOperationException("Metodo da implementare");
    }
}
