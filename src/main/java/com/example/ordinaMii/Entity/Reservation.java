package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "ReservationDate", nullable = false)
    private LocalDate reservationDate;

    @Column(name = "ReservationTime", nullable = false)
    private LocalTime reservationTime;

    @Column(name = "NumberOfPeople", nullable = false)
    private int numberOfPeople;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ReservationStatus status;

    @ManyToOne
    @JoinColumn(name = "UserId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "TableId", nullable = false)
    private RestaurantTable table;
}
