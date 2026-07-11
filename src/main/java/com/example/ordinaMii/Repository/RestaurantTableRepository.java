package com.example.ordinaMii.Repository;


import com.example.ordinaMii.Entity.RestaurantTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {

    boolean existsByNumber(int number);

    boolean existsByNumberAndIdNot(int number, UUID id);

    Page<RestaurantTable> findByActive(Boolean active, Pageable pageable);
}
