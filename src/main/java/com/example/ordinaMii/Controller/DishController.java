package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.DishRequestDTO;
import com.example.ordinaMii.DTO.Response.DishResponseDTO;
import com.example.ordinaMii.Entity.Enum.DishCategory;
import com.example.ordinaMii.Services.DishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping
    public ResponseEntity<List<DishResponseDTO>> getDishes(
            @RequestParam(required = false) DishCategory category,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String descr) {

        List<DishResponseDTO> dishes = dishService.getDishes(category, name, descr);
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
    public ResponseEntity<Void> deleteDish(@PathVariable UUID id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }
}