package com.super_tunes.user_service.service;

import com.super_tunes.user_service.dto.request.AuthLoginRequest;
import com.super_tunes.user_service.dto.request.AuthSignupRequest;
import com.super_tunes.user_service.dto.response.AuthResponse;
import com.super_tunes.user_service.entity.User;
import com.super_tunes.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder
    passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse signup(AuthSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists: " +
            request.email());
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username already exists: " +
            request.username());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getId(),
        savedUser.getUsername(), savedUser.getEmail());

        return new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getEmail()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(AuthLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(),
        user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(),
        user.getUsername(), user.getEmail());

        return new AuthResponse(
            token,
            user.getId(),
            user.getUsername(),
            user.getEmail()
        );
    }
}