package com.example.ordinaMii.Services;

import com.example.ordinaMii.Repository.DishRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class DishService {
    private final DishRepository dishRepository;
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }
}
