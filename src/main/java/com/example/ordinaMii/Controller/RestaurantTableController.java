package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.RestaurantTableRequestDTO;
import com.example.ordinaMii.DTO.Response.RestaurantTableResponseDTO;
import com.example.ordinaMii.Services.RestaurantTableService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tables")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    public RestaurantTableController(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    @GetMapping
    public ResponseEntity<List<RestaurantTableResponseDTO>> getTables() {
        List<RestaurantTableResponseDTO> tables = restaurantTableService.getTables();
        return ResponseEntity.ok(tables);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantTableResponseDTO> getTableById(@PathVariable UUID id) {
        RestaurantTableResponseDTO table = restaurantTableService.getTableById(id);
        return ResponseEntity.ok(table);
    }

    @PostMapping
    public ResponseEntity<RestaurantTableResponseDTO> createTable(
            @Valid @RequestBody RestaurantTableRequestDTO restaurantTableRequestDTO) {

        RestaurantTableResponseDTO table = restaurantTableService.createTable(restaurantTableRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(table);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantTableResponseDTO> updateTable(
            @PathVariable UUID id,
            @Valid @RequestBody RestaurantTableRequestDTO restaurantTableRequestDTO) {

        RestaurantTableResponseDTO table = restaurantTableService.updateTable(id, restaurantTableRequestDTO);
        return ResponseEntity.ok(table);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable UUID id) {
        restaurantTableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}