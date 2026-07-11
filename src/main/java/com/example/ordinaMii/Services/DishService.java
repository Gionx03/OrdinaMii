package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.DishRequestDTO;
import com.example.ordinaMii.Entity.Dish;
import com.example.ordinaMii.Entity.Enum.DishCategory;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.DishMapper;
import com.example.ordinaMii.Repository.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.ordinaMii.DTO.Response.DishResponseDTO;
import org.springframework.transaction.annotation.Transactional;

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


    @Transactional(readOnly = true)
    @Cacheable(
            value = "dishSearch",
            key = "'list_' + #category + '_' + #name + '_' " +
                    "+ #descr + '_' + #available + '_' " +
                    "+ #pageable.pageNumber + '_' " +
                    "+ #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<DishResponseDTO> getDishes(
            DishCategory category,
            String name,
            String descr,
            Boolean available,
            Pageable pageable) {

        log.info("Recupero lista piatti. category={}, name={}, descr={}, available={}",
                category, name, descr, available);

        Page<Dish> dishes = dishRepository.searchDishes(
                category,
                name,
                descr,
                available,
                pageable
        );

        return dishes.map(dishMapper::toResponseDTO);
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "dishById", key = "#id")
    public DishResponseDTO getDishById(UUID id) {

        log.info("Recupero piatto con id={}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piatto non trovato con id: " + id));

        return dishMapper.toResponseDTO(dish);
    }

    @Transactional()
    @Caching(
            evict = {
                    @CacheEvict(value = "dishSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "dishById", key = "#result.id()")
            }
    )

    public DishResponseDTO createDish(DishRequestDTO dishRequestDTO) {

        log.info("Creazione nuovo piatto con nome={}", dishRequestDTO.name());

        Dish dish = dishMapper.toEntity(dishRequestDTO);

        Dish savedDish = dishRepository.save(dish);

        log.info("Piatto creato con id={}", savedDish.getId());

        return dishMapper.toResponseDTO(savedDish);
    }


    @Transactional()
    @Caching(
            evict = {
                    @CacheEvict(value = "dishSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "dishById", key = "#id")
            }
    )

    public DishResponseDTO updateDish(UUID id, DishRequestDTO dishRequestDTO) {

        log.info("Aggiornamento piatto con id={}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piatto non trovato con id: " + id));

        dishMapper.updateEntity(dish, dishRequestDTO);

        Dish updatedDish = dishRepository.save(dish);

        log.info("Piatto aggiornato con id={}", updatedDish.getId());

        return dishMapper.toResponseDTO(updatedDish);
    }


    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "dishSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "dishById", key = "#id")
            }
    )
    public DishResponseDTO deleteDish(UUID id) {

        log.info("Disattivazione piatto con id={}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piatto non trovato con id: " + id));

        dish.setAvailable(false);

        Dish disabledDish = dishRepository.save(dish);

        log.info("Piatto disattivato con id={}", id);

        return dishMapper.toResponseDTO(disabledDish);
    }
}
