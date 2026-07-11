package com.example.ordinaMii.Services;

import com.example.ordinaMii.DTO.Request.UpdateMeRequestDTO;
import com.example.ordinaMii.DTO.Request.UserRequestDTO;
import com.example.ordinaMii.DTO.Response.UserResponseDTO;
import com.example.ordinaMii.Entity.Enum.Roles;
import com.example.ordinaMii.Entity.User;
import com.example.ordinaMii.Exceptions.BadRequestException;
import com.example.ordinaMii.Exceptions.ConflictException;
import com.example.ordinaMii.Exceptions.ResourceNotFoundException;
import com.example.ordinaMii.Mapper.UserMapper;
import com.example.ordinaMii.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
/*
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
    }*/

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "userById", key = "#id")
            }
    )
    public UserResponseDTO updateUser(UUID id, UserRequestDTO userRequestDTO) {

        log.info("Aggiornamento utente con id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato con id: " + id
                ));

        if (userRepository.existsByUsernameAndIdNot(userRequestDTO.username(), id)) {
            throw new ConflictException(
                    "Esiste già un altro utente con username: " + userRequestDTO.username()
            );
        }

        if (userRepository.existsByEmailAndIdNot(userRequestDTO.email(), id)) {
            throw new ConflictException(
                    "Esiste già un altro utente con email: " + userRequestDTO.email()
            );
        }

        userMapper.updateEntity(user, userRequestDTO);

        User updatedUser = userRepository.save(user);

        log.info("Utente aggiornato con id={}", updatedUser.getId());

        return userMapper.toResponseDTO(updatedUser);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userSearch", allEntries = true),
                    @CacheEvict(value = "userById", key = "#id")
            }
    )
    public void deleteUser(UUID id) {

        log.info("Eliminazione utente con id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utente non trovato con id: " + id
                ));

        userRepository.delete(user);

        log.info("Utente eliminato con id={}", id);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "userById", key = "#result.id()")
            }
    )
    public UserResponseDTO getOrCreateMe(Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());

        log.info("Recupero o creazione profilo utente autenticato con id={}", userId);

        User user = userRepository.findById(userId)
                .orElseGet(() -> createLocalUser(jwt));

        return userMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            value = "userSearch",
            key = "'list_' + #role + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort"
    )
    public Page<UserResponseDTO> getUsers(Roles role, Pageable pageable) {

        log.info("Recupero lista utenti. role={}", role);

        Page<User> users;

        if (role == null) {
            users = userRepository.findAll(pageable);
        } else {
            users = userRepository.findByRole(role, pageable);
        }

        return users.map(userMapper::toResponseDTO);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "userSearch", allEntries = true)
            },
            put = {
                    @CachePut(value = "userById", key = "#result.id()")
            }
    )
    public UserResponseDTO updateMe(Jwt jwt, UpdateMeRequestDTO updateMeRequestDTO) {

        UUID userId = UUID.fromString(jwt.getSubject());

        log.info("Aggiornamento profilo personale per userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseGet(() -> createLocalUser(jwt));

        user.setPhone(updateMeRequestDTO.phone());
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);

        log.info("Profilo personale aggiornato per userId={}", updatedUser.getId());

        return userMapper.toResponseDTO(updatedUser);
    }

    private String extractUsername(Jwt jwt) {

        String username = jwt.getClaimAsString("preferred_username");

        if (username != null && !username.isBlank()) {
            return username;
        }

        String email = jwt.getClaimAsString("email");

        if (email != null && !email.isBlank()) {
            return email;
        }

        return jwt.getSubject();
    }

    private String extractEmail(Jwt jwt) {

        String email = jwt.getClaimAsString("email");

        if (email == null || email.isBlank()) {
            throw new BadRequestException("Email non presente nel token Keycloak");
        }

        return email;
    }

    private Roles extractRole(Jwt jwt) {

        if (hasRole(jwt, "ADMIN")) {
            return Roles.ADMIN;
        }

        if (hasRole(jwt, "CUOCO")) {
            return Roles.CUOCO;
        }

        if (hasRole(jwt, "CAMERIERE")) {
            return Roles.CAMERIERE;
        }

        return Roles.CLIENTE;
    }

    private boolean hasRole(Jwt jwt, String roleName) {

        return hasRealmRole(jwt, roleName) || hasClientRole(jwt, roleName);
    }

    private boolean hasRealmRole(Jwt jwt, String roleName) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess == null) {
            return false;
        }

        Object rolesObject = realmAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return false;
        }

        return roles.stream()
                .map(Object::toString)
                .map(String::toUpperCase)
                .anyMatch(role -> role.equals(roleName));
    }

    private boolean hasClientRole(Jwt jwt, String roleName) {

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        if (resourceAccess == null) {
            return false;
        }

        Object clientObject = resourceAccess.get("OrdinaMii-Backend");

        if (!(clientObject instanceof Map<?, ?> clientAccess)) {
            return false;
        }

        Object rolesObject = clientAccess.get("roles");

        if (!(rolesObject instanceof List<?> roles)) {
            return false;
        }

        return roles.stream()
                .map(Object::toString)
                .map(String::toUpperCase)
                .anyMatch(role -> role.equals(roleName));
    }

    private User createLocalUser(Jwt jwt) {

        UUID userId = UUID.fromString(jwt.getSubject());
        String username = extractUsername(jwt);
        String email = extractEmail(jwt);
        Roles role = extractRole(jwt);

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Esiste già un utente con email: " + email);
        }

        if (userRepository.existsByUsername(username)) {
            throw new ConflictException("Esiste già un utente con username: " + username);
        }

        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPhone(null);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        User savedUser = userRepository.save(user);

        log.info("Profilo locale creato per utente autenticato con id={}", savedUser.getId());

        return savedUser;
    }
}