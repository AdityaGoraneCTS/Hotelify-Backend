package com.cts.user_service.service;

import com.cts.user_service.config.JwtUtility;
import com.cts.user_service.dto.AuthResponseDto;
import com.cts.user_service.dto.UserDto;
import com.cts.user_service.dto.UserResponseDto;
import com.cts.user_service.entity.Roles;
import com.cts.user_service.entity.User;
import com.cts.user_service.exception.EmailAlreadyExistsException;
import com.cts.user_service.exception.ResourceNotFoundException;
import com.cts.user_service.exception.UsernameAlreadyExistsException;
import com.cts.user_service.repository.RolesRepository;
import com.cts.user_service.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RolesRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtility jwtUtility;

    @Override
    public AuthResponseDto createUser(UserDto userDto) {
        // 1. Check for existing email with a specific exception
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyExistsException("An account with this email already exists: " + userDto.getEmail());
        }

        // 2. Check for existing username with its own specific exception
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken: " + userDto.getUsername());
        }

        // 3. Map DTO to entity and set initial values
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setLoyaltyPoints(0L); // Initialize loyalty points

        // 4. Find the role from the database (which should exist thanks to DataLoader)
        Optional<Roles> roleOptional = roleRepository.findByName(userDto.getRole());
        if (roleOptional.isEmpty()) {
            // This case should ideally not be hit if DataLoader runs successfully.
            // It points to a server configuration issue.
            throw new RuntimeException("Configuration error: Role '" + userDto.getRole() + "' not found in database.");
        }

        Set<Roles> roles = new HashSet<>();
        roles.add(roleOptional.get());
        user.setRoles(roles);

        // 5. Save the new user and return the response DTO
        User savedUser = userRepository.save(user);

        // --- THE NEW LOGIC ---
        // 1. Immediately generate a token for the new user
        String token = jwtUtility.generateToken(savedUser.getId(), savedUser.getEmail(), savedUser.getRoles().stream().findFirst().get().getName());

        // 2. Convert the saved user to the response DTO
        UserResponseDto userResponse = convertToUserResponseDto(savedUser);

        // 3. Return both the token and the user data
        return new AuthResponseDto(token, userResponse);
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToUserResponseDto(user);
    }

    @Override
    public UserResponseDto updateUser(String id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        modelMapper.map(userDto, user);
        
        User updatedUser = userRepository.save(user);
        return convertToUserResponseDto(updatedUser);
    }

    private UserResponseDto convertToUserResponseDto(User user) {
        UserResponseDto dto = modelMapper.map(user, UserResponseDto.class);
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            dto.setRole(user.getRoles().iterator().next().getName());
        }
        return dto;
    }

    @Override
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto updateLoyaltyPoints(String userId, Long points) {
        // 1. Use the new repository method to update points directly
        int updatedRows = userRepository.updateLoyaltyPoints(userId, points);

        // 2. Check if any user was actually updated
        if (updatedRows == 0) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        // 3. Fetch the updated user to return its new state
        User updatedUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User disappeared after update with id: " + userId));

        // 4. Convert to DTO and return
        return convertToUserResponseDto(updatedUser);
    }

}