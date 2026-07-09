package com.example.ordinaMii.Mapper;

import com.example.ordinaMii.DTO.Request.UserRequestDTO;
import com.example.ordinaMii.DTO.Response.UserLightResponseDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {

        if (dto == null) {
            return null;
        }

        User user = new User();

        user.setId(dto.id());
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return user;
    }

    public void updateEntity(User user, UserRequestDTO dto) {

        if (user == null || dto == null) {
            return;
        }

        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setUpdatedAt(LocalDateTime.now());
    }

    public UserResponseDTO toResponseDTO(User user) {

        if (user == null) {
            return null;
        }

        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserLightResponseDTO toLightResponseDTO(User user) {

        if (user == null) {
            return null;
        }

        return UserLightResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }

}