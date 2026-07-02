package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.RestaurantTableRequestDTO;
import com.example.ordinaMii.DTO.Response.RestaurantTableResponseDTO;
import com.example.ordinaMii.Repository.RestaurantTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository) {
        this.restaurantTableRepository = restaurantTableRepository;
    }

    public List<RestaurantTableResponseDTO> getTables() {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public RestaurantTableResponseDTO getTableById(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public RestaurantTableResponseDTO createTable(RestaurantTableRequestDTO restaurantTableRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public RestaurantTableResponseDTO updateTable(UUID id, RestaurantTableRequestDTO restaurantTableRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public void deleteTable(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }
}
