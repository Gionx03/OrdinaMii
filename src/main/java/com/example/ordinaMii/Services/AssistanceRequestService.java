package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.AssistanceRequestDTO;
import com.example.ordinaMii.DTO.Request.AssistanceRequestStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.AssistanceRequestResponseDTO;
import com.example.ordinaMii.Entity.AssistanceRequest;
import com.example.ordinaMii.Entity.Enum.AssistanceRequestStatus;
import com.example.ordinaMii.Entity.RestaurantTable;
import com.example.ordinaMii.Exceptions.BadRequestException;
import com.example.ordinaMii.Exceptions.ConflictException;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.AssistanceRequestMapper;
import com.example.ordinaMii.Repository.AssistanceRequestRepository;
import com.example.ordinaMii.Repository.RestaurantTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class AssistanceRequestService {

    private final AssistanceRequestRepository assistanceRequestRepository;
    private final RestaurantTableRepository restaurantTableRepository;
    private final AssistanceRequestMapper assistanceRequestMapper;

    public AssistanceRequestService(AssistanceRequestRepository assistanceRequestRepository,
                                    RestaurantTableRepository restaurantTableRepository,
                                    AssistanceRequestMapper assistanceRequestMapper) {
        this.assistanceRequestRepository = assistanceRequestRepository;
        this.restaurantTableRepository = restaurantTableRepository;
        this.assistanceRequestMapper = assistanceRequestMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "assistanceRequestSearch",
            key = "'list_' + #status + '_' + #tableId + '_' " +
                    "+ #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<AssistanceRequestResponseDTO> getAssistanceRequests(
            AssistanceRequestStatus status,
            UUID tableId,
            Pageable pageable) {

        log.info("Recupero lista richieste assistenza. status={}, tableId={}",
                status, tableId);

        Page<AssistanceRequest> assistanceRequests =
                assistanceRequestRepository.searchAssistanceRequests(status, tableId, pageable);

        return assistanceRequests.map(assistanceRequestMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "assistanceRequestById", key = "#id")
    public AssistanceRequestResponseDTO getAssistanceRequestById(UUID id) {

        log.info("Recupero richiesta assistenza con id={}", id);

        AssistanceRequest assistanceRequest = assistanceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Richiesta assistenza non trovata con id: " + id
                ));

        return assistanceRequestMapper.toResponseDTO(assistanceRequest);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "assistanceRequestSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "assistanceRequestById", key = "#result.id()")
            }
    )
    public AssistanceRequestResponseDTO createAssistanceRequest(
            AssistanceRequestDTO assistanceRequestDTO) {

        log.info("Creazione richiesta assistenza per tableId={}",
                assistanceRequestDTO.tableId());

        RestaurantTable table = restaurantTableRepository.findById(assistanceRequestDTO.tableId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tavolo non trovato con id: " + assistanceRequestDTO.tableId()
                ));
        if (!table.isActive()) {
            throw new BadRequestException(
                    "Non è possibile creare una richiesta di assistenza per un tavolo disattivato"
            );
        }

        AssistanceRequest assistanceRequest = assistanceRequestMapper.toEntity(
                assistanceRequestDTO,
                table,
                AssistanceRequestStatus.PENDING
        );

        AssistanceRequest savedAssistanceRequest =
                assistanceRequestRepository.save(assistanceRequest);

        log.info("Richiesta assistenza creata con id={}",
                savedAssistanceRequest.getId());

        return assistanceRequestMapper.toResponseDTO(savedAssistanceRequest);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "assistanceRequestSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "assistanceRequestById", key = "#id")
            }
    )
    public AssistanceRequestResponseDTO updateAssistanceRequestStatus(
            UUID id,
            AssistanceRequestStatusUpdateRequestDTO assistanceRequestStatusUpdateRequestDTO) {

        log.info("Aggiornamento stato richiesta assistenza con id={}, nuovo stato={}",
                id,
                assistanceRequestStatusUpdateRequestDTO.status());

        AssistanceRequest assistanceRequest = assistanceRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Richiesta assistenza non trovata con id: " + id
                ));

        AssistanceRequestStatus newStatus = assistanceRequestStatusUpdateRequestDTO.status();

        validateAssistanceRequestStatusTransition(assistanceRequest, newStatus);

        assistanceRequest.setStatus(newStatus);
        updateResolvedAt(assistanceRequest, newStatus);

        AssistanceRequest updatedAssistanceRequest =
                assistanceRequestRepository.save(assistanceRequest);

        log.info("Stato richiesta assistenza aggiornato con id={}",
                updatedAssistanceRequest.getId());

        return assistanceRequestMapper.toResponseDTO(updatedAssistanceRequest);
    }


    private void validateAssistanceRequestStatusTransition(
            AssistanceRequest assistanceRequest,
            AssistanceRequestStatus newStatus) {

        if (newStatus == null) {
            throw new BadRequestException("Lo stato della richiesta è obbligatorio");
        }

        AssistanceRequestStatus currentStatus = assistanceRequest.getStatus();

        if (currentStatus == AssistanceRequestStatus.RESOLVED) {
            throw new ConflictException(
                    "Non è possibile modificare una richiesta di assistenza già risolta"
            );
        }

        if (currentStatus == AssistanceRequestStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare una richiesta di assistenza cancellata"
            );
        }

        if (currentStatus == AssistanceRequestStatus.PENDING
                && newStatus == AssistanceRequestStatus.PENDING) {
            throw new ConflictException(
                    "La richiesta di assistenza è già in attesa"
            );
        }
    }


    private void updateResolvedAt(
            AssistanceRequest assistanceRequest,
            AssistanceRequestStatus status) {

        if (status == AssistanceRequestStatus.PENDING) {
            assistanceRequest.setResolvedAt(null);
            return;
        }

        if (status == AssistanceRequestStatus.RESOLVED ||
                status == AssistanceRequestStatus.CANCELLED) {

            assistanceRequest.setResolvedAt(LocalDateTime.now());
        }
    }
}