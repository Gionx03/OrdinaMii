package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.AssistanceRequestDTO;
import com.example.ordinaMii.DTO.Request.AssistanceRequestStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.AssistanceRequestResponseDTO;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import com.example.ordinaMii.Services.AssistanceRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/assistance-requests")
public class AssistanceRequestController {

    private final AssistanceRequestService assistanceRequestService;

    public AssistanceRequestController(AssistanceRequestService assistanceRequestService) {
        this.assistanceRequestService = assistanceRequestService;
    }

    @GetMapping
    public ResponseEntity<Page<AssistanceRequestResponseDTO>> getAssistanceRequests(
            @RequestParam(required = false) AssistanceRequestStatus status,
            @RequestParam(name = "table_id", required = false) UUID tableId,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {

        Page<AssistanceRequestResponseDTO> assistanceRequests =
                assistanceRequestService.getAssistanceRequests(status, tableId, pageable);

        return ResponseEntity.ok(assistanceRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssistanceRequestResponseDTO> getAssistanceRequestById(
            @PathVariable UUID id) {

        AssistanceRequestResponseDTO assistanceRequest =
                assistanceRequestService.getAssistanceRequestById(id);

        return ResponseEntity.ok(assistanceRequest);
    }

    @PostMapping
    public ResponseEntity<AssistanceRequestResponseDTO> createAssistanceRequest(
            @Valid @RequestBody AssistanceRequestDTO assistanceRequestDTO) {

        AssistanceRequestResponseDTO assistanceRequest =
                assistanceRequestService.createAssistanceRequest(assistanceRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(assistanceRequest);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AssistanceRequestResponseDTO> updateAssistanceRequestStatus(
            @PathVariable UUID id,
            @Valid @RequestBody AssistanceRequestStatusUpdateRequestDTO assistanceRequestStatusUpdateRequestDTO) {

        AssistanceRequestResponseDTO assistanceRequest =
                assistanceRequestService.updateAssistanceRequestStatus(id, assistanceRequestStatusUpdateRequestDTO);

        return ResponseEntity.ok(assistanceRequest);
    }
}