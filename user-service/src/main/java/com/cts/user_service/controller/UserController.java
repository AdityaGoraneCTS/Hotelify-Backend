package com.cts.user_service.controller;

import com.cts.user_service.config.JwtUtility;
import com.cts.user_service.dto.*;
import com.cts.user_service.entity.User;
import com.cts.user_service.repository.UserRepository;
import com.cts.user_service.service.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserDto userDto) {
        AuthResponseDto response = userService.createUser(userDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody LoginDto loginDto) {
        // The authenticate method will throw an exception if credentials are bad,
        // so the code below will only execute on successful authentication.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

        // 1. Get the username/email from the authenticated principal
        String loginIdentifier = authentication.getName();

        // 2. Fetch the full User entity from the database using the login identifier
        //    This is necessary to get the UUID and email for the JWT.
        User authenticatedUser = userRepository.findByUsername(loginIdentifier)
                .or(() -> userRepository.findByEmail(loginIdentifier))
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found in database: " + loginIdentifier));


        // 3. Extract the required information for the token from the fetched User entity
        String userId = authenticatedUser.getId(); // The UUID String
        String email = authenticatedUser.getEmail();

        // 4. Extract the role cleanly from the authentication object
        String authority = authentication.getAuthorities().stream()
                .findFirst() // Get the first role
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new IllegalStateException("User has no roles assigned"));

        UserResponseDto userResponseDto = modelMapper.map(authenticatedUser, UserResponseDto.class);
        if (authenticatedUser.getRoles() != null && !authenticatedUser.getRoles().isEmpty()) {
            userResponseDto.setRole(authenticatedUser.getRoles().iterator().next().getName());
        }

        // 5. Call generateToken with the correct 3 arguments
        String token = jwtUtility.generateToken(userId, email, authority);
        return new AuthResponseDto(token, userResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        try {
            List<UserResponseDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable String id) {
        try {
            UserResponseDto user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/profile")
    public String userProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            return "Welcome " + authentication.getName();
        }
        return "Welcome Guest";
    }

    @PatchMapping("/users/{userId}/loyalty-points")
    public ResponseEntity<UserResponseDto> updateUserLoyaltyPoints(
            @PathVariable String userId,
            @Valid @RequestBody LoyaltyPointsUpdateDto loyaltyPointsUpdateDto) {

        UserResponseDto updatedUser = userService.updateLoyaltyPoints(userId, loyaltyPointsUpdateDto.getLoyaltyPoints());
        return ResponseEntity.ok(updatedUser);
    }
}