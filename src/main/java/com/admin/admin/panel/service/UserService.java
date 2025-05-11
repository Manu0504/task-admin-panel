package com.admin.admin.panel.service;

import com.admin.admin.panel.model.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto createUser(UserDto userDto);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);

    // OAuth2 support methods
    UserDto findOrCreateGoogleUser(String email, String username);
    UserDto findByEmail(String email);
    UserDto findByUsername(String username);
}