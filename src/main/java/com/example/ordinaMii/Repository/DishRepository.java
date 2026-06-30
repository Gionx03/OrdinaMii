package com.example.ordinaMii.Repository;

import com.example.ordinaMii.Entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {
    @Query(value = """
            SELECT *
            FROM dish d
            WHERE (:category IS NULL OR LOWER(d.category) = LOWER(:category))
            AND (:name IS NULL OR LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')))
            AND (:descr IS NULL OR LOWER(d.description) LIKE LOWER(CONCAT('%', :descr, '%')))
            ORDER BY d.name
            """, nativeQuery = true)
    List<Dish> searchDishes(@Param("category") String category,
                            @Param("name") String name,
                            @Param("descr") String descr);
}
