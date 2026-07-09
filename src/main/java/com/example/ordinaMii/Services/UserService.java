package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.UserRequestDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Entity.User;
import com.example.ordinaMii.Exceptions.ConflictException;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.UserMapper;
import com.example.ordinaMii.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userById", key = "#id")
    public UserResponseDTO getUserById(UUID id) {

        log.info("Recupero utente con id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Utente non trovato con id: " + id));

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userById", key = "#userId")
    public UserResponseDTO getMe(UUID userId) {

        log.info("Recupero profilo utente autenticato con id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Utente autenticato non trovato con id: " + userId));

        return userMapper.toResponseDTO(user);
    }

    @Transactional
    @CachePut(value = "userById", key = "#result.id()")
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {

        log.info("Creazione nuovo utente con id={}, username={}",
                userRequestDTO.id(),
                userRequestDTO.username());

        if (userRepository.existsById(userRequestDTO.id())) {
            throw new ConflictException
                    ("Esiste già un utente con id: " + userRequestDTO.id());
        }

        if (userRepository.existsByUsername(userRequestDTO.username())) {
            throw new ConflictException
                    ("Esiste già un utente con username: " + userRequestDTO.username());
        }

        if (userRepository.existsByEmail(userRequestDTO.email())) {
            throw new ConflictException
                    ("Esiste già un utente con email: " + userRequestDTO.email());
        }

        User user = userMapper.toEntity(userRequestDTO);

        User savedUser = userRepository.save(user);

        log.info("Utente creato con id={}", savedUser.getId());

        return userMapper.toResponseDTO(savedUser);
    }

    @Transactional
    @CachePut(value = "userById", key = "#id")
    public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) {

        log.info("Aggiornamento utente con id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException
                        ("Utente non trovato con id: " + id));

        if (userRepository.existsByUsernameAndIdNot(userRequestDTO.username(), id)) {
            throw new ConflictException
                    ("Esiste già un altro utente con username: " + userRequestDTO.username());
        }

        if (userRepository.existsByEmailAndIdNot(userRequestDTO.email(), id)) {
            throw new ConflictException
                    ("Esiste già un altro utente con email: " + userRequestDTO.email());
        }

        userMapper.updateEntity(user, userRequestDTO);

        log.info("Utente aggiornato con id={}", user.getId());

        return userMapper.toResponseDTO(user);
    }

    @Transactional
    @CacheEvict(value = "userById", key = "#id")
    public void deleteUser(UUID id) {

        log.info("Eliminazione utente con id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con id: " + id));

        userRepository.delete(user);

        log.info("Utente eliminato con id={}", id);
    }
}