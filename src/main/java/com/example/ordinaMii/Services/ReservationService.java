package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.MyReservationRequestDTO;
import com.example.ordinaMii.DTO.Request.ReservationRequestDTO;
import com.example.ordinaMii.DTO.Request.ReservationStatusUpdateRequestDTO;
import com.example.ordinaMii.DTO.Response.ReservationResponseDTO;
import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Entity.Reservation;
import com.example.ordinaMii.Entity.RestaurantTable;
import com.example.ordinaMii.Entity.User;
import com.example.ordinaMii.Exceptions.BadRequestException;
import com.example.ordinaMii.Exceptions.ConflictException;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.ReservationMapper;
import com.example.ordinaMii.Repository.ReservationRepository;
import com.example.ordinaMii.Repository.RestaurantTableRepository;
import com.example.ordinaMii.Repository.UserRepository;
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
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RestaurantTableRepository restaurantTableRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, ReservationMapper reservationMapper, RestaurantTableRepository restaurantTableRepository, UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.restaurantTableRepository = restaurantTableRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "reservationSearch",
            key = "'list_' + #status + '_' + #userId + '_' +" +
                    " #tableId + '_' + #date + " +
                    "'_' + #pageable.pageNumber + '_' +" +
                    " #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<ReservationResponseDTO> getReservations(
            ReservationStatus status,
            UUID userId,
            UUID tableId,
            LocalDate date,
            Pageable pageable) {

        log.info("Recupero lista prenotazioni. status={}, userId={}, tableId={}, date={}",
                status, userId, tableId, date);

        Page<Reservation> reservations = date == null
                ? reservationRepository.searchReservations(
                status,
                userId,
                tableId,
                pageable
        )
                : reservationRepository.searchReservationsByDate(
                status,
                userId,
                tableId,
                date,
                pageable
        );

        return reservations.map(reservationMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "reservationById", key = "#id")
    public ReservationResponseDTO getReservationById(UUID id) {

        log.info("Recupero prenotazione con id={}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Prenotazione non trovata con id: " + id));

        return reservationMapper.toResponseDTO(reservation);
    }



    @Transactional(readOnly = true)
    @Cacheable(
            value = "reservationTableSearch",
            key = "'table_' + #tableId + '_' " +
                    "+ #date + '_' " +
                    "+ #pageable.pageNumber + '_' " +
                    "+ #pageable.pageSize + '_' " +
                    "+ #pageable.sort"
    )
    public Page<ReservationResponseDTO> getReservationsByTable(
            UUID tableId,
            LocalDate date,
            Pageable pageable) {

        log.info("Recupero prenotazioni per tavolo con id={}, date={}", tableId, date);

        if (!restaurantTableRepository.existsById(tableId)) {
            throw new ResourceNotFoundException("Tavolo non trovato con id: " + tableId);
        }

        Page<Reservation> reservations = date == null
                ? reservationRepository.searchReservations(
                null,
                null,
                tableId,
                pageable
        )
                : reservationRepository.searchReservationsByDate(
                null,
                null,
                tableId,
                date,
                pageable
        );

        return reservations.map(reservationMapper::toResponseDTO);
    }



    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "reservationSearch", allEntries = true),
                    @CacheEvict(value = "reservationTableSearch", allEntries = true),
                    @CacheEvict(value = "myReservationSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "reservationById", key = "#result.id()")
            }
    )
    public ReservationResponseDTO createReservation(ReservationRequestDTO reservationRequestDTO) {

        log.info("Creazione nuova prenotazione. userId={}, tableId={}, date={}, time={}",
                reservationRequestDTO.userId(),
                reservationRequestDTO.tableId(),
                reservationRequestDTO.date(),
                reservationRequestDTO.time());

        return createReservationInternal(
                reservationRequestDTO.userId(),
                reservationRequestDTO.date(),
                reservationRequestDTO.time(),
                reservationRequestDTO.numberOfPeople(),
                reservationRequestDTO.tableId()
        );
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "reservationSearch", allEntries = true),
                    @CacheEvict(value = "reservationTableSearch", allEntries = true),
                    @CacheEvict(value = "myReservationSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "reservationById", key = "#result.id()")
            }
    )
    public ReservationResponseDTO createMyReservation(
            UUID userId,
            MyReservationRequestDTO myReservationRequestDTO) {

        log.info("Creazione nuova prenotazione personale. userId={}, tableId={}, date={}, time={}",
                userId,
                myReservationRequestDTO.tableId(),
                myReservationRequestDTO.date(),
                myReservationRequestDTO.time());

        return createReservationInternal(
                userId,
                myReservationRequestDTO.date(),
                myReservationRequestDTO.time(),
                myReservationRequestDTO.numberOfPeople(),
                myReservationRequestDTO.tableId()
        );
    }


    private ReservationResponseDTO createReservationInternal(
            UUID userId,
            LocalDate date,
            LocalTime time,
            int numberOfPeople,
            UUID tableId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato con id: " + userId
                ));

        RestaurantTable table = restaurantTableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tavolo non trovato con id: " + tableId
                ));
        if (!table.isActive()) {
            throw new BadRequestException(
                    "Non è possibile prenotare un tavolo disattivato"
            );
        }

        if (numberOfPeople > table.getSeats()) {
            throw new BadRequestException(
                    "Il numero di persone supera i posti disponibili del tavolo"
            );
        }

        if (reservationRepository.existsByTable_IdAndDateAndTimeAndStatusNot(
                tableId,
                date,
                time,
                ReservationStatus.CANCELLED
        )) {
            throw new ConflictException(
                    "Esiste già una prenotazione attiva per questo tavolo nella stessa data e ora"
            );
        }

        Reservation reservation = new Reservation();

        reservation.setDate(date);
        reservation.setTime(time);
        reservation.setNumberOfPeople(numberOfPeople);
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        Reservation savedReservation = reservationRepository.save(reservation);

        log.info("Prenotazione creata con id={}", savedReservation.getId());

        return reservationMapper.toResponseDTO(savedReservation);
    }



    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "reservationSearch", allEntries = true),
                    @CacheEvict(value = "myReservationSearch", allEntries = true),
                    @CacheEvict(value = "reservationTableSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "reservationById", key = "#id")
            }
    )
    public ReservationResponseDTO updateReservation(UUID id, ReservationRequestDTO reservationRequestDTO) {

        log.info("Aggiornamento prenotazione con id={}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Prenotazione non trovata con id: " + id));

        validateReservationCanBeModified(reservation);

        User user = userRepository.findById(reservationRequestDTO.userId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Utente non trovato con id: " + reservationRequestDTO.userId()));

        RestaurantTable table = restaurantTableRepository.findById(reservationRequestDTO.tableId())
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Tavolo non trovato con id: " + reservationRequestDTO.tableId()));
        if (!table.isActive()) {
            throw new BadRequestException(
                    "Non è possibile prenotare un tavolo disattivato"
            );
        }

        if (reservationRequestDTO.numberOfPeople() > table.getSeats()) {
            throw new BadRequestException
                    ("Il numero di persone supera i posti disponibili del tavolo");
        }

        if (reservationRepository.existsByTable_IdAndDateAndTimeAndIdNotAndStatusNot(
                reservationRequestDTO.tableId(),
                reservationRequestDTO.date(),
                reservationRequestDTO.time(),
                id,
                ReservationStatus.CANCELLED
        )) {

            throw new ConflictException
                    ("Esiste già un'altra prenotazione attiva per questo tavolo nella stessa data e ora");
        }

        reservationMapper.updateEntity(reservation, reservationRequestDTO, user, table);

        log.info("Prenotazione aggiornata con id={}", reservation.getId());

        return reservationMapper.toResponseDTO(reservation);
    }




    private void validateReservationCanBeModified(Reservation reservation) {

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare una prenotazione cancellata"
            );
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ConflictException(
                    "Non è possibile modificare una prenotazione già completata"
            );
        }
    }




    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "reservationSearch", allEntries = true),
                    @CacheEvict(value = "reservationTableSearch", allEntries = true),
                    @CacheEvict(value = "myReservationSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "reservationById", key = "#id")
            }
    )
    public ReservationResponseDTO updateReservationStatus(
            UUID id,
            ReservationStatusUpdateRequestDTO reservationStatusUpdateRequestDTO) {

        log.info("Aggiornamento stato prenotazione con id={}, nuovo stato={}",
                id,
                reservationStatusUpdateRequestDTO.status());

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prenotazione non trovata con id: " + id
                ));

        validateReservationStatusTransition(
                reservation,
                reservationStatusUpdateRequestDTO.status()
        );

        reservation.setStatus(reservationStatusUpdateRequestDTO.status());

        Reservation updatedReservation = reservationRepository.save(reservation);

        log.info("Stato prenotazione aggiornato con id={}", updatedReservation.getId());

        return reservationMapper.toResponseDTO(updatedReservation);
    }


    private void validateReservationStatusTransition(
            Reservation reservation,
            ReservationStatus newStatus) {

        if (newStatus == null) {
            throw new BadRequestException("Lo stato della prenotazione è obbligatorio");
        }

        ReservationStatus currentStatus = reservation.getStatus();

        if (currentStatus == ReservationStatus.CANCELLED) {
            throw new ConflictException(
                    "Non è possibile modificare lo stato di una prenotazione cancellata"
            );
        }

        if (currentStatus == ReservationStatus.COMPLETED) {
            throw new ConflictException(
                    "Non è possibile modificare lo stato di una prenotazione già completata"
            );
        }

        if (currentStatus == ReservationStatus.CONFIRMED
                && newStatus == ReservationStatus.CONFIRMED) {
            throw new ConflictException(
                    "La prenotazione è già confermata"
            );
        }

        if (newStatus == ReservationStatus.COMPLETED
                && isReservationInFuture(reservation)) {
            throw new ConflictException(
                    "Non è possibile completare una prenotazione futura"
            );
        }
    }


    private boolean isReservationInFuture(Reservation reservation) {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (reservation.getDate().isAfter(today)) {
            return true;
        }

        return reservation.getDate().isEqual(today)
                && reservation.getTime().isAfter(now);
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "reservationSearch", allEntries = true),
                    @CacheEvict(value = "reservationTableSearch", allEntries = true),
                    @CacheEvict(value = "myReservationSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "reservationById", key = "#id")
            }
    )
    public ReservationResponseDTO deleteReservation(UUID id) {

        log.info("Cancellazione logica prenotazione con id={}", id);

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Prenotazione non trovata con id: " + id
                ));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new ConflictException(
                    "La prenotazione è già cancellata"
            );
        }

        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            throw new ConflictException(
                    "Non è possibile cancellare una prenotazione già completata"
            );
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        Reservation cancelledReservation = reservationRepository.save(reservation);

        log.info("Prenotazione cancellata logicamente con id={}", cancelledReservation.getId());

        return reservationMapper.toResponseDTO(cancelledReservation);
    }


    @Transactional(readOnly = true)
    @Cacheable(
            value = "myReservationSearch",
            key = "'user_' + #userId + '_' + #startDate + '_' " +
                    "+ #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<ReservationResponseDTO> getReservationsByUser(
            UUID userId,
            LocalDate startDate,
            Pageable pageable) {

        log.info("Recupero prenotazioni per userId={}, startDate={}", userId, startDate);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utente non trovato con id: " + userId);
        }

        LocalDate normalizedStartDate = startDate == null
                ? LocalDate.of(1900, 1, 1)
                : startDate;

        Page<Reservation> reservations =
                reservationRepository.searchReservationsFromDate(
                        null,
                        userId,
                        null,
                        normalizedStartDate,
                        pageable
                );

        return reservations.map(reservationMapper::toResponseDTO);
    }
}
