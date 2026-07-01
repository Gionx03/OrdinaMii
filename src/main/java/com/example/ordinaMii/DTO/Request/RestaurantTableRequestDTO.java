package com.example.ordinaMii.DTO.Request;

import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record RestaurantTableRequestDTO(@Min(value = 1, message = "Il numero del tavolo deve essere maggiore di 0")
                                        int number,

                                        @Min(value = 1, message = "Il numero di posti deve essere maggiore di 0")
                                        int seats) {
}
