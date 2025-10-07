package com.cts.user_service.service;

import com.cts.user_service.dto.AuthResponseDto;
import com.cts.user_service.dto.UserDto;
import com.cts.user_service.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    AuthResponseDto createUser(UserDto userDto);
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(String id);
    UserResponseDto updateUser(String id, UserDto userDto);
    void deleteUser(String id);
    UserResponseDto updateLoyaltyPoints(String userId, Long points);
}