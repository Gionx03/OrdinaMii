package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"table_id", "date", "time"}))
public class Reservation {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDate date;

    @Column(name="ora",  nullable = false)
    private LocalTime time;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var r = (Reservation) o;

        if (id == null || r.id == null) {
            return false;
        }

        return id.equals(r.id);
    }


    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", date=" + date +
                ", time=" + time +
                ", numberOfPeople=" + numberOfPeople +
                ", status=" + status +
                ", userId=" +  user.getId()  +
                ", tableId=" + table.getId()  +
                '}';
    }
}
