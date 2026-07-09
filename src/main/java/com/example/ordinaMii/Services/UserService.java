package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.UserRequestDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Mapper.UserMapper;
import com.example.ordinaMii.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponseDTO getUserById(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public UserResponseDTO getMe(UUID userId) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }

    public void deleteUser(UUID id) {
        throw new UnsupportedOperationException("Metodo da implementare");
    }
}
