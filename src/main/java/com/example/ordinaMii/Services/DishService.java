package com.example.ordinaMii.Services;

import com.example.ordinaMii.Repository.DishRepository;
import org.springframework.stereotype.Service;

@Service
public class DishService {
    private final DishRepository dishRepository;
    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }
}
