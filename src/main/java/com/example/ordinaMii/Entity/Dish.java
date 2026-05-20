package com.example.ordinaMii.Entity;

import com.example.ordinaMii.Entity.Enum.DishCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Description")
    private String description;

    @Column(name = "Price", nullable = false)
    private double price;

    @Column(name = "Available", nullable = false)
    private boolean available;

    @Enumerated(EnumType.STRING)
    @Column(name = "Category", nullable = false)
    private DishCategory category;
}
