package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.DishRequestDTO;
import com.example.ordinaMii.DTO.Response.DishResponseDTO;
import com.example.ordinaMii.Entity.Enum.DishCategory;
import com.example.ordinaMii.Services.DishService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<Page<DishResponseDTO>> getDishes(
            @RequestParam(required = false) DishCategory category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String descr,
            @RequestParam(required = false, defaultValue = "true") Boolean available,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        Page<DishResponseDTO> dishes = dishService.getDishes(
                category,
                name,
                descr,
                available,
                pageable
        );

        return ResponseEntity.ok(dishes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishResponseDTO> getDishById(@PathVariable UUID id) {
        DishResponseDTO dish = dishService.getDishById(id);
        return ResponseEntity.ok(dish);
    }

    @PostMapping
    public ResponseEntity<DishResponseDTO> createDish(@Valid @RequestBody DishRequestDTO dishRequestDTO) {
        DishResponseDTO dish = dishService.createDish(dishRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(dish);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishResponseDTO> updateDish(
            @PathVariable UUID id,
            @Valid @RequestBody DishRequestDTO dishRequestDTO) {

        DishResponseDTO dish = dishService.updateDish(id, dishRequestDTO);
        return ResponseEntity.ok(dish);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DishResponseDTO> deleteDish(@PathVariable UUID id) {
        DishResponseDTO dish = dishService.deleteDish(id);
        return ResponseEntity.ok(dish);
    }
}