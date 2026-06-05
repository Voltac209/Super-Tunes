package com.super_tunes.user_service.controller;

import com.super_tunes.user_service.dto.request.AuthLoginRequest;
import com.super_tunes.user_service.dto.request.AuthSignupRequest;
import com.super_tunes.user_service.dto.response.AuthResponse;
import com.super_tunes.user_service.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid
    AuthSignupRequest request) {
        return
        ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid
    AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}