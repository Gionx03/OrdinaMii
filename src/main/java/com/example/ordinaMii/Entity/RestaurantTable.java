package com.example.ordinaMii.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantTable {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "Number", nullable = false, unique = true)
    private int number;

    @Column(name = "Seats", nullable = false)
    private int seats;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        var table = (RestaurantTable) o;

        if (id == null || table.id == null) {
            return false;
        }

        return id.equals(table.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "RestaurantTable{" +
                "id=" + id +
                ", number=" + number +
                ", seats=" + seats +
                '}';
    }
}
