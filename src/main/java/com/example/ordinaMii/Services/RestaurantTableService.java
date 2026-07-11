package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.RestaurantTableRequestDTO;
import com.example.ordinaMii.DTO.Response.RestaurantTableResponseDTO;
import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Entity.RestaurantTable;
import com.example.ordinaMii.Exceptions.ConflictException;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.RestaurantTableMapper;
import com.example.ordinaMii.Repository.ReservationRepository;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Slf4j
@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantTableMapper restaurantTableMapper;
    private final ReservationRepository reservationRepository;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository,
                                  RestaurantTableMapper restaurantTableMapper,
                                  ReservationRepository reservationRepository) {

        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantTableMapper = restaurantTableMapper;
        this.reservationRepository = reservationRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "tableSearch",
            key = "'list_' + #active + '_' " +
                    "+ #pageable.pageNumber + '_' " +
                    "+ #pageable.pageSize + '_' " +
                    "+ #pageable.sort"
    )
    public Page<RestaurantTableResponseDTO> getTables(Boolean active, Pageable pageable) {

        log.info("Recupero lista tavoli. active={}", active);

        Page<RestaurantTable> tables;

        if (active == null) {
            tables = restaurantTableRepository.findAll(pageable);
        } else {
            tables = restaurantTableRepository.findByActive(active, pageable);
        }

        return tables.map(restaurantTableMapper::toResponseDTO);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "tableById", key = "#id")
    public RestaurantTableResponseDTO getTableById(UUID id) {

        log.info("Recupero tavolo con id={}", id);

        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tavolo non trovato con id: " + id));

        return restaurantTableMapper.toResponseDTO(table);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "tableSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "tableById", key = "#result.id()")
            }
    )
    public RestaurantTableResponseDTO createTable(RestaurantTableRequestDTO restaurantTableRequestDTO) {

        log.info("Creazione nuovo tavolo con numero={}", restaurantTableRequestDTO.number());

        if (restaurantTableRepository.existsByNumber(restaurantTableRequestDTO.number())) {
            throw new ConflictException("Esiste già un tavolo con numero: " + restaurantTableRequestDTO.number());
        }

        RestaurantTable table = restaurantTableMapper.toEntity(restaurantTableRequestDTO);

        RestaurantTable savedTable = restaurantTableRepository.save(table);

        log.info("Tavolo creato con id={}", savedTable.getId());

        return restaurantTableMapper.toResponseDTO(savedTable);
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "tableSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "tableById", key = "#id")
            }
    )
    public RestaurantTableResponseDTO updateTable(UUID id, RestaurantTableRequestDTO restaurantTableRequestDTO) {

        log.info("Aggiornamento tavolo con id={}", id);

        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tavolo non trovato con id: " + id));

        if (restaurantTableRepository.existsByNumberAndIdNot(restaurantTableRequestDTO.number(), id)) {
            throw new ConflictException("Esiste già un altro tavolo con numero: " + restaurantTableRequestDTO.number());
        }

        restaurantTableMapper.updateEntity(table, restaurantTableRequestDTO);

        RestaurantTable updatedTable = restaurantTableRepository.save(table);

        log.info("Tavolo aggiornato con id={}", updatedTable.getId());

        return restaurantTableMapper.toResponseDTO(updatedTable);
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "tableSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "tableById", key = "#id")
            }
    )
    public RestaurantTableResponseDTO deleteTable(UUID id) {

        log.info("Disattivazione tavolo con id={}", id);

        RestaurantTable table = restaurantTableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tavolo non trovato con id: " + id
                ));

        if (!table.isActive()) {
            throw new ConflictException(
                    "Il tavolo è già disattivato"
            );
        }

        if (reservationRepository.existsFutureActiveReservationByTable(
                id,
                LocalDate.now(),
                LocalTime.now(),
                ReservationStatus.CANCELLED
        )) {
            throw new ConflictException(
                    "Non è possibile disattivare un tavolo con prenotazioni future attive"
            );
        }

        table.setActive(false);

        RestaurantTable disabledTable = restaurantTableRepository.save(table);

        log.info("Tavolo disattivato con id={}", id);

        return restaurantTableMapper.toResponseDTO(disabledTable);
    }
}
