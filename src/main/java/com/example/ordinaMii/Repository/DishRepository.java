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
        AND (:name = '' OR LOWER(d.name) LIKE CONCAT('%', :name, '%'))
        AND (:descr = '' OR LOWER(d.description) LIKE CONCAT('%', :descr, '%'))
        AND (:available IS NULL OR d.available = :available)
        ORDER BY d.name ASC
        """)
    Page<Dish> searchDishes(
            @Param("category") DishCategory category,
            @Param("name") String name,
            @Param("descr") String descr,
            @Param("available") Boolean available,
            Pageable pageable
    );
}