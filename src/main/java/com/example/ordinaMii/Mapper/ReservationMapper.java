package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.ReservationRequestDTO;
import com.example.ordinaMii.DTO.Response.ReservationResponseDTO;
import com.example.ordinaMii.Entity.Enum.ReservationStatus;
import com.example.ordinaMii.Entity.Reservation;
import com.example.ordinaMii.Entity.RestaurantTable;
import com.example.ordinaMii.Entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservationMapper {

    private final UserMapper userMapper;
    private final RestaurantTableMapper restaurantTableMapper;

    public ReservationMapper(UserMapper userMapper,
                             RestaurantTableMapper restaurantTableMapper) {
        this.userMapper = userMapper;
        this.restaurantTableMapper = restaurantTableMapper;
    }

    public Reservation toEntity(ReservationRequestDTO dto,
                                User user,
                                RestaurantTable table,
                                ReservationStatus status) {

        if (dto == null) {
            return null;
        }

        Reservation reservation = new Reservation();

        reservation.setDate(dto.date());
        reservation.setTime(dto.time());
        reservation.setNumberOfPeople(dto.numberOfPeople());
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setStatus(status);

        return reservation;
    }

    public void updateEntity(Reservation reservation,
                             ReservationRequestDTO dto,
                             User user,
                             RestaurantTable table) {

        if (reservation == null || dto == null) {
            return;
        }

        reservation.setDate(dto.date());
        reservation.setTime(dto.time());
        reservation.setNumberOfPeople(dto.numberOfPeople());
        reservation.setUser(user);
        reservation.setTable(table);
    }

    public ReservationResponseDTO toResponseDTO(Reservation reservation) {

        if (reservation == null) {
            return null;
        }

        return ReservationResponseDTO.builder()
                .id(reservation.getId())
                .date(reservation.getDate())
                .time(reservation.getTime())
                .numberOfPeople(reservation.getNumberOfPeople())
                .status(reservation.getStatus())
                .user(userMapper.toLightResponseDTO(reservation.getUser()))
                .table(restaurantTableMapper.toResponseDTO(reservation.getTable()))
                .build();
    }

}