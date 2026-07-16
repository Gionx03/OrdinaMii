package com.example.ordinaMii.Controller;

import com.example.ordinaMii.DTO.Request.RestaurantTableRequestDTO;
import com.example.ordinaMii.DTO.Response.RestaurantTableResponseDTO;
import com.example.ordinaMii.Services.RestaurantTableService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tables")
public class RestaurantTableController {

    private final RestaurantTableService restaurantTableService;

    public RestaurantTableController(RestaurantTableService restaurantTableService) {
        this.restaurantTableService = restaurantTableService;
    }

    @GetMapping
    public ResponseEntity<Page<RestaurantTableResponseDTO>> getTables(
            @RequestParam(required = false, defaultValue = "true") Boolean active,
            @PageableDefault(size = 10, sort = "number") Pageable pageable) {

        Page<RestaurantTableResponseDTO> tables = restaurantTableService.getTables(active, pageable);
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
    public ResponseEntity<RestaurantTableResponseDTO> deleteTable(@PathVariable UUID id) {
        RestaurantTableResponseDTO table = restaurantTableService.deleteTable(id);
        return ResponseEntity.ok(table);
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<RestaurantTableResponseDTO> reactivateTable(
            @PathVariable UUID id) {

        RestaurantTableResponseDTO table =
                restaurantTableService.reactivateTable(id);

        return ResponseEntity.ok(table);
    }
}