package com.admin.admin.panel.controller;


import com.admin.admin.panel.model.UserDto;
import com.admin.admin.panel.security.jwt.JwtTokenProvider;
import com.admin.admin.panel.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public AuthController(@Lazy AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserServiceImpl userServiceImpl) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        try {
            String username = loginRequest.get("username");
            String password = loginRequest.get("password");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = jwtTokenProvider.generateToken(authentication);
            return ResponseEntity.ok(Map.of("token", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }


    /**
     * Public registration endpoint for new users.
     * Expects JSON: { "username": "...", "email": "...", "password": "...", "role": "USER" }
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> registrationRequest) {
        String username = registrationRequest.get("username");
        String email = registrationRequest.get("email");
        String password = registrationRequest.get("password");
        String role = registrationRequest.getOrDefault("role", "USER");

        // Check if user already exists
        if (userServiceImpl.findByEmail(email) != null || userServiceImpl.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        UserDto userDto = new UserDto();
        userDto.setUsername(username);
        userDto.setEmail(email);
        userDto.setRole(role);
        // Store encoded password as needed (if you store passwords)
        // For demo, we skip password storage, but you should store it securely

        userServiceImpl.createUser(userDto);

        return ResponseEntity.ok("User registered successfully");
    }
}