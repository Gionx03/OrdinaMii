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

    @Cacheable(
            value = "dishes_search",
            key = "'list_' + #category + '_' + #name + '_' " +
                    "+ #descr + '_' + #pageable.pageNumber + '_' " +
                    "+ #pageable.pageSize + '_' + #pageable.sort"
    )

    public Page<DishResponseDTO> getDishes(
            DishCategory category,
            String name,
            String descr,
            Pageable pageable) {

        log.info("Recupero lista piatti. category={}, name={}, descr={}", category, name, descr);

        Page<Dish> dishes = dishRepository.searchDishes(category, name, descr, pageable);

        return dishes.map(dishMapper::toResponseDTO);
    }

    @Cacheable(value = "dishesById", key = "#id")

    public DishResponseDTO getDishById(UUID id) {

        log.info("Recupero piatto con id={}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piatto non trovato con id: " + id));

        return dishMapper.toResponseDTO(dish);
    }
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

    @Caching(
            evict = {
                    @CacheEvict(value = "dishById", key = "#id"),
                    @CacheEvict(value = "dishSearch", allEntries = true)
            }
    )

    public void deleteDish(UUID id) {

        log.info("Eliminazione piatto con id={}", id);

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Piatto non trovato con id: " + id));

        dishRepository.delete(dish);

        log.info("Piatto eliminato con id={}", id);
    }
}
