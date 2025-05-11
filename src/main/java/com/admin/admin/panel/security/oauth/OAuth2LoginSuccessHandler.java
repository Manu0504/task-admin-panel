package com.admin.admin.panel.security.oauth;

import com.admin.admin.panel.model.UserDto;
import com.admin.admin.panel.security.jwt.JwtTokenProvider;
import com.admin.admin.panel.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public OAuth2LoginSuccessHandler(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        // Use email as username for Google users for consistency
        String username = email;

        // Check if user exists in DB
        UserDto user = userService.findByEmail(email);
        if (user == null) {
            // Signup: create new user
            user = new UserDto();
            user.setUsername(username);
            user.setEmail(email);
            user.setRole("USER"); // Default role
            user = userService.createUser(user);
        }

        // Issue JWT for the user
        String jwt = jwtTokenProvider.generateToken(user);

        // Return JSON response instead of redirect
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = String.format("{\"message\": \"Login success\", \"token\": \"%s\"}", jwt);
        response.getWriter().write(json);
        response.getWriter().flush();
    }
}