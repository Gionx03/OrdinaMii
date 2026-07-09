package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.DishRequestDTO;
import com.example.ordinaMii.Entity.Enum.DishCategory;
import com.example.ordinaMii.Mapper.DishMapper;
import com.example.ordinaMii.Repository.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.ordinaMii.DTO.Response.DishResponseDTO;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DishService {

    private final DishRepository dishRepository;
    private final DishMapper dishMapper;

    public DishService(DishRepository dishRepository, DishMapper dishMapper) {
        this.dishRepository = dishRepository;
        this.dishMapper = dishMapper;
    }

    public Page<DishResponseDTO> getDishes(
            DishCategory category,
            String name,
            String descr,
            Pageable pageable) {

        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public DishResponseDTO getDishById(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public DishResponseDTO createDish(DishRequestDTO dishRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public DishResponseDTO updateDish(UUID id, DishRequestDTO dishRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public void deleteDish(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }
}
