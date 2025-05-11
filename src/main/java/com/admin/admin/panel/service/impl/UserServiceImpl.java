package com.admin.admin.panel.service.impl;

import com.admin.admin.panel.model.UserDto;
import com.admin.admin.panel.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserServiceImpl implements UserService {

    private final Map<Long, UserDto> users = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    public List<UserDto> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        long id = idGenerator.incrementAndGet();
        userDto.setId(id);
        users.put(id, userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        userDto.setId(id);
        users.put(id, userDto);
        return userDto;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    @Override
    public UserDto findOrCreateGoogleUser(String email, String username) {
        UserDto user = findByEmail(email);
        if (user == null) {
            user = new UserDto();
            long id = idGenerator.incrementAndGet();
            user.setId(id);
            // Always use email as username for Google users for consistency
            user.setUsername(email);
            user.setEmail(email);
            user.setRole("ADMIN"); // Default role for Google users
            users.put(id, user);
        }
        return user;
    }

    @Override
    public UserDto findByEmail(String email) {
        return users.values().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public UserDto findByUsername(String username) {
        return users.values().stream()
                .filter(u -> username.equalsIgnoreCase(u.getUsername()))
                .findFirst()
                .orElse(null);
    }

}