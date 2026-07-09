package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Dish;
import com.example.ordinaMii.Entity.Enum.DishCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface DishRepository extends JpaRepository<Dish, UUID> {

    @Query("""
            SELECT d
            FROM Dish d
            WHERE (:category IS NULL OR d.category = :category)
            AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:descr IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :descr, '%')))
            """)
    Page<Dish> searchDishes(
            @Param("category") DishCategory category,
            @Param("name") String name,
            @Param("descr") String descr,
            Pageable pageable
    );
}