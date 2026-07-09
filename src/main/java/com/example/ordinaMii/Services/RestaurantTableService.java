package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.RestaurantTableRequestDTO;
import com.example.ordinaMii.DTO.Response.RestaurantTableResponseDTO;
import com.example.ordinaMii.Mapper.RestaurantTableMapper;
import com.example.ordinaMii.Repository.RestaurantTableRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class RestaurantTableService {

    private final RestaurantTableRepository restaurantTableRepository;
    private final RestaurantTableMapper restaurantTableMapper;

    public RestaurantTableService(RestaurantTableRepository restaurantTableRepository, RestaurantTableMapper restaurantTableMapper) {
        this.restaurantTableRepository = restaurantTableRepository;
        this.restaurantTableMapper = restaurantTableMapper;
    }

    public Page<RestaurantTableResponseDTO> getTables(Pageable pageable) {
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
